// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen

import com.google.common.collect.ImmutableSet
import com.google.common.io.Resources
import groovy.text.Template
import groovy.text.TemplateEngine
import groovy.text.XmlTemplateEngine
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import javax.inject.Inject
import org.fidata.logogen.shared.configurations.Default
import org.fidata.logogen.shared.configurations.Hebrew
import org.fidata.logogen.shared.configurations.Rtl
import org.fidata.logogen.shared.enums.HebrewLogoGenerationMethod
import org.fidata.logogen.shared.enums.RtlLogoGenerationMethod
import org.fidata.logogen.shared.properties.ConfigurableDefault
import org.fidata.logogen.shared.properties.ConfigurableHebrew
import org.fidata.logogen.shared.properties.ConfigurableRtl
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.logging.LogLevel
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.IsolationMode
import org.gradle.workers.WorkerConfiguration
import org.gradle.workers.WorkerExecutor
import org.im4java.core.ConvertCmd
import org.im4java.core.IMOperation

/**
 * Logo Generator
 */
@CompileStatic
abstract class AbstractGenerator implements Plugin<Project> {
  private Project project

  @Override
  final void apply(Project project) {
    this.@project = project
  }

  /**
   * Name of generator. It's used as names of a task and output directory.
   * It is recommended to use {@code camelCase} for the name
   * TOTHINK: better use name separated with dots or dashes, so we could use the same name for plugin id
   */
  abstract String getName()

  /**
   * Family groups generators with the same target.
   * Family is used for generated archive names, and it is expected
   * that outputs of all generators with the same classifier
   * are used by the same consumer
   * TODO
   */
  @SuppressWarnings('GrMethodMayBeStatic')
  String getFamily() { null }

  /**
   * Class implementing generator's converter.
   * Generator should have at most one converter
   */
  abstract Class<? extends AbstractGeneratorTask> getConverterImplClass()

  // TODO
  protected final CopySpec copySpec(@DelegatesTo(CopySpec) Closure closure) {
    project.copySpec closure
  }

  // TODO
  abstract Map<String, CopySpec> getOutputs(Provider<? extends ConfigurableDefault> /* TODO */ configuration)

  /**
   * Extension with configuration of generator
   */
  Class<?> getExtensionClass() { null }

  static final class Families {
    public static final String WEBSITE = 'website'
    // Operating Systems
    public static final String WINDOWS = 'windows'
    public static final String ANDROID = 'android'
    public static final String IOS = 'ios'
    public static final String BLACKBERRY = 'blackberry'
  }

  /**
   * Converter takes a logo in SVG format
   * and generates an image in format required
   * by target operating system, website or service,
   * and also auxiliary files
   */
  @CacheableTask
  abstract static class AbstractGeneratorTask<ConfigurationClass extends Default, ConfigurableClass extends Provider<? extends ConfigurationClass>> extends DefaultTask implements ConfigurableDefault {
    private final WorkerExecutor workerExecutor

    protected abstract ConfigurableClass getConfiguration()

    /**
     * Output directory
     */
    @OutputDirectory
    final DirectoryProperty outputDir = project.objects.directoryProperty()

    protected Set<Class<? extends ImageMagickConvertOperation<ConfigurationClass>>> getImageMagicConvertOperationClasses() {
      ImmutableSet.of()
    }
    protected Set<Class<? extends GenerateXmlOperation>> getGenerateXmlOperationClasses() {
      ImmutableSet.of()
    }

    protected abstract Class<OutputLayout> getOutputLayoutClass()

    AbstractGeneratorTask(WorkerExecutor workerExecutor) {
      this.@workerExecutor = workerExecutor
    }

    @TaskAction
    protected final void run() {
      imageMagicConvertOperationClasses.each { Class<? extends ImageMagickConvertOperation> imageMagickConvertOperationClass ->
        workerExecutor.submit(imageMagickConvertOperationClass, new Action<WorkerConfiguration>() {
          @Override
          void execute(WorkerConfiguration workerConfiguration) {
            workerConfiguration.isolationMode = IsolationMode.NONE
            workerConfiguration.params(((project.logging.level ?: project.gradle.startParameter.logLevel) <= LogLevel.DEBUG), outputDir.get().asFile, configuration.get())
          }
        })
      }
      // TODO: Unite these
      generateXmlOperationClasses.each { Class<? extends GenerateXmlOperation> generateXmlOperationClass ->
        workerExecutor.submit(generateXmlOperationClass, new Action<WorkerConfiguration>() {
          @Override
          void execute(WorkerConfiguration workerConfiguration) {
            workerConfiguration.isolationMode = IsolationMode.NONE
            workerConfiguration.params(((project.logging.level ?: project.gradle.startParameter.logLevel) <= LogLevel.DEBUG), outputDir.get().asFile, configuration.get())
          }
        })
      }
    }

    /**
     * ImageMagick {@code convert} operation
     *
     * <b>Constructor of class inheriting this one should have
     * {@code srcFile}, {@code debug} and {@code outputDir} as first three arguments
     * and pass them to {@code super} constructor.
     * All other arguments may be placed after these three.</b>
     */
    abstract static class ImageMagickConvertOperation<ConfigurationClass extends Default> implements Runnable {
      private static final ConvertCmd CONVERT_CMD = new ConvertCmd()
  
      private final boolean debug
      protected final File outputDir
      protected final ConfigurationClass configuration
      private final OutputLayout<ConfigurationClass> outputLayout

      /**
       *
       * @param srcFile
       * @param debug
       */
      @Inject
      ImageMagickConvertOperation(boolean debug = false, File outputDir, ConfigurationClass configuration, Class<? extends OutputLayout<ConfigurationClass>> outputLayoutClass) {
        this.@debug = debug
        this.@outputDir = outputDir
        this.@configuration = configuration
        this.@outputLayout = outputLayoutClass.newInstance()
      }
  
      @Override
      final void run() {
        IMOperation operation = new IMOperation()
        if (debug) {
          operation.verbose()
        }
        // operation.define('stream:buffer-size=0') // We may need this to avoid cache problems
        operation.addImage(configuration.srcFile.toString())
        IMOperation suboperation = this.operation
        suboperation.write(outputLayout.getOutputDirFileName(configuration, [:] /* TODO */).toString())
        operation.addSubOperation(suboperation)
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
      final void run() {
        outputFile.text = template.make(bindings)
      }

      /**
       * Map of template variable bindings
       * @return
       */
      abstract protected Map getBindings()
    }
  
    /*protected final void exec(List<String> commandLine) {
      project.exec { ExecSpec execSpec ->
        execSpec.commandLine = commandLine
      }
    }*/
  
    protected final void copy(Object into, Closure rename) { // TODO
      project.copy { CopySpec copySpec ->
        copySpec.from srcFile
        copySpec.into into
        copySpec.rename rename
      }
    }

    abstract static class OutputLayout<ConfigurationClass extends Default> {
      abstract File getOutputDirFileName(ConfigurationClass configuration, Map<String, ?> context)
    }
  }

  /**
   * Logo Generator that is able to generate a separate icon for RTL locale
   */
  @InheritConstructors
  abstract static class AbstractGeneratorTaskWithRtl<ConfigurationClass extends Rtl, ConfigurableClass extends Provider<? extends ConfigurationClass>> extends AbstractGeneratorTask implements ConfigurableRtl {
    @Override
    protected Set<Class<? extends ImageMagickConvertOperation<ConfigurationClass>>> getImageMagicConvertOperationClasses() {
      ImmutableSet.of()
    }

    { // TODO
      rtlLogoGenerationMethod.convention project.providers.provider {
        rtlSrcFile.present ? RtlLogoGenerationMethod.SEPARATE_SOURCE : RtlLogoGenerationMethod.MIRROW
      }
    }

    /**
     * ImageMagick {@code convert} operation
     *
     * <b>Constructor of class inheriting this one should have
     * {@code srcFile}, {@code debug}, {@code outputDir}, {@code rtlSrcFile} and {@code rtlLogoGenerationMethod}
     * as first five arguments and pass them to {@code super} constructor.
     * All other arguments may be placed after these five.</b>
     */
    @InheritConstructors
    abstract static class ImageMagickConvertOperation<ConfigurationClass extends Rtl> extends AbstractGeneratorTask.ImageMagickConvertOperation<ConfigurationClass> {
      // TODO
    }
  }

  /**
   * Logo Generator that is able to generate a separate icons for RTL and Hebrew locales
   */
  @InheritConstructors
  abstract static class AbstractGeneratorTaskWithRtlAndHebrew<ConfigurationClass extends Hebrew, ConfigurableClass extends Provider<? extends ConfigurationClass>> extends AbstractGeneratorTaskWithRtl implements ConfigurableHebrew {
    @Override
    protected Set<Class<? extends ImageMagickConvertOperation<ConfigurationClass>>> getImageMagicConvertOperationClasses() {
      ImmutableSet.of()
    }

    { // TODO
      hebrewLogoGenerationMethod.convention project.providers.provider {
        hebrewSrcFile.present ? HebrewLogoGenerationMethod.SEPARATE_SOURCE : HebrewLogoGenerationMethod.STANDARD_RTL
      }
    }

    /**
     * ImageMagick {@code convert} operation
     *
     * <b>Constructor of class inheriting this one should have
     * {@code srcFile}, {@code debug}, {@code outputDir}, {@code rtlSrcFile}, {@code rtlLogoGenerationMethod},
     * {@code hebrewSrcFile} and {@code hebrewLogoGenerationMethod}
     * as first seven arguments and pass them to {@code super} constructor.
     * All other arguments may be placed after these seven.</b>
     */
    @InheritConstructors
    abstract static class ImageMagickConvertOperation<ConfigurationClass extends Hebrew> extends AbstractGeneratorTaskWithRtl.ImageMagickConvertOperation<ConfigurationClass> {
      // TODO
    }
  }
}
