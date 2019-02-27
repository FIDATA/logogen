#!/usr/bin/env groovy
/*
 * Converter Gradle task class
 * Copyright © 2015, 2018-2019  Basil Peace
 *
 * This file is part of Logo Generator.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.fidata.logogen.generators

import com.google.common.io.Resources
import groovy.text.Template
import groovy.text.TemplateEngine
import groovy.text.XmlTemplateEngine
import groovy.transform.CompileStatic
import org.fidata.logogen.LogoGeneratorsExtension
import org.fidata.logogen.shared.LogoConfigurationProvider
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.CopySpec
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.workers.IsolationMode
import org.gradle.workers.WorkerConfiguration
import org.gradle.workers.WorkerExecutor
import org.im4java.core.ConvertCmd
import org.im4java.core.IMOperation
import javax.inject.Inject
import org.gradle.api.tasks.TaskAction

/**
 * Logo Generator takes a logo in SVG format
 * and generates an image in format required
 * by target operating system, website or service,
 * and also auxiliary files
 */
@CacheableTask
@CompileStatic
abstract class Converter extends DefaultTask {
  protected <T> T getProjectExtension(Class<T> extensionClass) {
    ((ExtensionAware)project.extensions.findByType(LogoGeneratorsExtension)).extensions.getByType(extensionClass)
  }

  static final class Classifiers {
    public static final String WEBSITE = 'website'
    // Operating Systems
    public static final String WINDOWS = 'windows'
    public static final String ANDROID = 'android'
    public static final String IOS = 'ios'
    public static final String BLACKBERRY = 'blackberry'
  }

  @Delegate(methodAnnotations = true)
  private final LogoConfigurationProvider logoConfigurationProvider = new LogoConfigurationProvider(project.objects)

  /**
   * Output directory
   */
  @OutputDirectory
  final DirectoryProperty outputDir = project.objects.directoryProperty()

  /**
   * ImageMagick {@code convert} operation
   *
   * <b>Constructor of class inheriting this one should have
   * {@code srcFile}, {@code debug} and {@code outputDir} as first three arguments
   * and pass them to {@code super} constructor.
   * All other arguments may be placed after these three.</b>
   */
  abstract protected static class ImageMagickConvertOperation implements Runnable {
    private static final ConvertCmd CONVERT_CMD = new ConvertCmd()

    private final File srcFile
    private final boolean debug
    protected final File outputDir

    /**
     *
     * @param srcFile
     * @param debug
     */
    @Inject
    ImageMagickConvertOperation(File srcFile, boolean debug = false, File outputDir) {
      this.@srcFile = srcFile
      this.@debug = debug
      this.@outputDir = outputDir
    }

    @Override
    final void run() {
      IMOperation operation = new IMOperation()
      if (debug) {
        operation.verbose()
      }
      // operation.define('stream:buffer-size=0') // We may need this to avoid cache problems
      operation.addImage(srcFile.toString())
      operation.addSubOperation(this.operation)
      CONVERT_CMD.run(operation)
    }

    /**
     * Gets actual operation done on image.
     * Should be implemented by classes inheriting this one
     */
    protected abstract IMOperation getOperation()
  }

  /**
   * Generation of XML file from existing template.
   * Template should be placed in resources, and implementation
   * should pass resource name as a first argument to {@code super} constructor
   */
  abstract protected static class GenerateXmlOperation implements Runnable {
    private static final TemplateEngine TEMPLATE_ENGINE = new XmlTemplateEngine()
    private final Template template
    private final File outputFile

    @Inject
    GenerateXmlOperation(String resourceName, File outputFile) {
      this.@template = TEMPLATE_ENGINE.createTemplate(Resources.getResource(resourceName))
      this.@outputFile = outputFile
    }

    @Override
    void run() {
      outputFile.text = template.make(bindings)
    }

    abstract protected Map getBindings()
  }

  /*protected final void exec(List<String> commandLine) {
    project.exec { ExecSpec execSpec ->
      execSpec.commandLine = commandLine
    }
  }*/

  /**
   * Adds {@link ImageMagickConvertOperation} to specified {@link WorkerExecutor}.
   * This should be called from method marked with {@link TaskAction}
   * in generator implementation.
   *
   * @param workerExecutor {@link WorkerExecutor} which will run the operation
   * @param actionClass Actual operation class extending {@link ImageMagickConvertOperation}
   * @param params Params to pass to {@code actionClass} constructor
   */
  protected void imageMagicConvert(WorkerExecutor workerExecutor, Class<? extends ImageMagickConvertOperation> actionClass, Object... params) {
    workerExecutor.submit(actionClass, new Action<WorkerConfiguration>() {
      @Override
      void execute(WorkerConfiguration workerConfiguration) {
        workerConfiguration.isolationMode = IsolationMode.NONE
        workerConfiguration.params srcFile.get().asFile, (project.logging.level ?: project.gradle.startParameter.logLevel) <= LogLevel.DEBUG, outputDir.get().asFile, *params
      }
    })
  }

  /**
   * Adds {@link GenerateXmlOperation} to specified {@link WorkerExecutor}.
   * This should be called from method marked with {@link TaskAction}
   * in generator implementation.
   *
   * @param workerExecutor {@link WorkerExecutor} which will run the operation
   * @param actionClass Actual operation class extending {@link GenerateXmlOperation}
   * @param params Params to pass to {@code actionClass} constructor
   */
  protected void generateXml(WorkerExecutor workerExecutor, Class<? extends GenerateXmlOperation> actionClass, Object... params) {
    workerExecutor.submit(actionClass, new Action<WorkerConfiguration>() {
      @Override
      void execute(WorkerConfiguration workerConfiguration) {
        workerConfiguration.isolationMode = IsolationMode.NONE
        workerConfiguration.params *params
      }
    })
  }

  protected final void copy(Object into, Closure rename) {
    project.copy { CopySpec copySpec ->
      copySpec.from srcFile
      copySpec.into into
      copySpec.rename rename
    }
  }
}
