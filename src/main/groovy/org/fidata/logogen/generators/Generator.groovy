// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators

import com.google.common.io.Resources
import groovy.text.Template
import groovy.text.TemplateEngine
import groovy.text.XmlTemplateEngine
import groovy.transform.CompileStatic
import org.fidata.logogen.annotations.DelegateWithGradleAnnotations
import org.fidata.logogen.shared.Default
import org.fidata.logogen.shared.HebrewConfigurationProperty
import org.fidata.logogen.shared.HebrewLogoGenerationMethod
import org.fidata.logogen.shared.DefaultConfigurationProperty
import org.fidata.logogen.shared.RtlConfigurationProperty
import org.fidata.logogen.shared.RtlLogoGenerationMethod
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
import org.gradle.workers.IsolationMode
import org.gradle.workers.WorkerConfiguration
import org.gradle.workers.WorkerExecutor
import org.im4java.core.ConvertCmd
import org.im4java.core.IMOperation
import javax.inject.Inject

/**
 * Logo Generator
 */
@CompileStatic
abstract class Generator implements Plugin<Project> {
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
  abstract Class<? extends AbstractConverter> getConverterImplClass()

  protected final CopySpec copySpec(@DelegatesTo(CopySpec) Closure closure) {
    project.copySpec closure
  }

  abstract Map<String, CopySpec> getOutputs(Provider<? extends Default> /* TODO */ configuration)

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
  @CompileStatic
  abstract private static class AbstractConverter<Extension> extends DefaultTask implements Default {
    abstract void convention(Extension extension)

    @DelegateWithGradleAnnotations
    private final DefaultConfigurationProperty defaultConfigurationProvider = new DefaultConfigurationProperty(project.objects, project.providers)
  
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
  
    /**
     * Adds {@link ImageMagickConvertOperation} to specified {@link org.gradle.workers.WorkerExecutor}.
     * This should be called from method marked with {@link org.gradle.api.tasks.TaskAction}
     * in generator implementation.
     *
     * @param workerExecutor {@link org.gradle.workers.WorkerExecutor} which will run the operation
     * @param actionClass Actual operation class extending {@link ImageMagickConvertOperation}
     * @param params Params to pass to {@code actionClass} constructor
     */
    protected void imageMagickConvert(WorkerExecutor workerExecutor, Class<? extends ImageMagickConvertOperation> actionClass, Object... params) {
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
     * This should be called from method marked with {@link org.gradle.api.tasks.TaskAction}
     * in generator implementation.
     *
     * @param workerExecutor {@link WorkerExecutor} which will run the operation
     * @param actionClass Actual operation class extending {@link GenerateXmlOperation}
     * @param params Params to pass to {@code actionClass} constructor
     */
    protected final void generateXml(WorkerExecutor workerExecutor, Class<? extends GenerateXmlOperation> actionClass, Object... params) {
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

  abstract static class Converter<Extension> extends AbstractConverter<Extension> {
    /**
     * @inheritDocs
     */
    @Override
    protected final void imageMagickConvert(WorkerExecutor workerExecutor, Class<? extends ImageMagickConvertOperation> actionClass, Object... params) {
      super.imageMagickConvert workerExecutor, actionClass, params
    }
  }

  /**
   * Logo Generator that is able to generate a separate icon for RTL locale
   */
  @CompileStatic
  abstract private static class AbstractConverterWithRtl<Extension> extends AbstractConverter<Extension> {
    @DelegateWithGradleAnnotations
    private final RtlConfigurationProperty rtlLogoConfigurationProvider
    {
      rtlLogoConfigurationProvider = new RtlConfigurationProperty(project.objects, project.providers)
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
    abstract protected static class ImageMagickConvertOperation extends AbstractConverter.ImageMagickConvertOperation {
      private File rtlSrcFile
      private RtlLogoGenerationMethod rtlLogoGenerationMethod

      @Inject
      ImageMagickConvertOperation(File srcFile, boolean debug = false, File outputDir, File rtlSrcFile = null, RtlLogoGenerationMethod rtlLogoGenerationMethod) {
        super(srcFile, debug, outputDir)
        this.@rtlSrcFile = rtlSrcFile
        this.@rtlLogoGenerationMethod = rtlLogoGenerationMethod
      }
    }

    /**
     * @inheritDocs
     */

    protected void imageMagickConvert(WorkerExecutor workerExecutor, Class<? extends ImageMagickConvertOperation> actionClass, Object... params) {
      Object[] newParams = new Object[params.length + 2]
      newParams[0] = rtlSrcFile.orNull
      newParams[1] = rtlLogoGenerationMethod.get()
      System.arraycopy(params, 0, newParams, 2, params.length)
      super.imageMagickConvert(workerExecutor, actionClass, newParams)
    }
  }

  abstract static class ConverterWithRtl<Extension> extends AbstractConverterWithRtl<Extension> {
    /**
     * @inheritDocs
     */
    @Override
    protected final void imageMagickConvert(WorkerExecutor workerExecutor, Class<? extends ImageMagickConvertOperation> actionClass, Object... params) {
      super.imageMagickConvert workerExecutor, actionClass, params
    }
  }

  /**
   * Logo Generator that is able to generate a separate icons for RTL and Hebrew locales
   */
  @CompileStatic
  abstract private static class AbstractConverterWithRtlAndHebrew<Extension> extends AbstractConverterWithRtl<Extension> {
    @DelegateWithGradleAnnotations
    private final HebrewConfigurationProperty hebrewLogoConfigurationProvider
    {
      hebrewLogoConfigurationProvider = new HebrewConfigurationProperty(project.objects, project.providers)
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
    abstract protected static class ImageMagickConvertOperation extends AbstractConverterWithRtl.ImageMagickConvertOperation {
      private File hebrewSrcFile
      private HebrewLogoGenerationMethod hebrewLogoGenerationMethod

      @Inject
      ImageMagickConvertOperation(File srcFile, boolean debug = false, File outputDir, File rtlSrcFile = null, RtlLogoGenerationMethod rtlIconGenerationMethod, File hebrewSrcFile = null, HebrewLogoGenerationMethod hebrewLogoGenerationMethod) {
        super(srcFile, debug, outputDir, rtlSrcFile, rtlIconGenerationMethod)
        this.@hebrewSrcFile = hebrewSrcFile
        this.@hebrewLogoGenerationMethod = hebrewLogoGenerationMethod
      }
    }

    // TOTEST
    /**
     * @inheritDocs
     */
    protected void imageMagickConvert(WorkerExecutor workerExecutor, Class<? extends ImageMagickConvertOperation> actionClass, Object... params) {
      Object[] newParams = new Object[params.length + 2]
      newParams[0] = hebrewSrcFile.orNull
      newParams[1] = hebrewLogoGenerationMethod.get()
      System.arraycopy(params, 0, newParams, 2, params.length)
      super.imageMagickConvert(workerExecutor, actionClass, newParams)
    }
  }

  abstract static class ConverterWithRtlAndHebrew<Extension> extends AbstractConverterWithRtlAndHebrew<Extension> {
    /**
     * @inheritDocs
     */
    @Override
    protected final void imageMagickConvert(WorkerExecutor workerExecutor, Class<? extends ImageMagickConvertOperation> actionClass, Object... params) {
      super.imageMagickConvert workerExecutor, actionClass, params
    }
  }
}
