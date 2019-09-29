// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators

import com.google.common.io.Resources
import groovy.text.Template
import groovy.text.TemplateEngine
import groovy.text.XmlTemplateEngine
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import javax.inject.Inject
import org.fidata.gradle.utils.DelegateWithGradleAnnotations
import org.fidata.logogen.shared.configurations.impl.DefaultImpl
import org.fidata.logogen.shared.configurations.impl.HebrewImpl
import org.fidata.logogen.shared.configurations.impl.RtlImpl
import org.fidata.logogen.shared.enums.HebrewLogoGenerationMethod
import org.fidata.logogen.shared.enums.RtlLogoGenerationMethod
import org.fidata.logogen.shared.properties.ConfigurableDefault
import org.fidata.logogen.shared.properties.ConfigurableHebrew
import org.fidata.logogen.shared.properties.ConfigurableRtl
import org.fidata.logogen.shared.properties.impl.DefaultPropertyImpl
import org.fidata.logogen.shared.properties.impl.HebrewPropertyImpl
import org.fidata.logogen.shared.properties.impl.RtlPropertyImpl
import org.fidata.logogen.shared.providers.DefaultProvider
import org.fidata.logogen.shared.providers.HebrewProvider
import org.fidata.logogen.shared.providers.RtlProvider
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
  @CompileStatic
  abstract private static class AbstractConverter extends DefaultTask {
    @DelegateWithGradleAnnotations(includeTypes = [ConfigurableDefault])
    private final DefaultProvider defaultProperty = new DefaultPropertyImpl(project.objects, project.providers, project.layout)
  
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
  
      private final boolean debug
      protected final File outputDir
      private final DefaultImpl defaultConfiguration

      /**
       *
       * @param srcFile
       * @param debug
       */
      @Inject
      ImageMagickConvertOperation(boolean debug = false, File outputDir, DefaultImpl defaultConfiguration) {
        this.@debug = debug
        this.@outputDir = outputDir
        this.@defaultConfiguration = defaultConfiguration
      }
  
      @Override
      final void run() {
        IMOperation operation = new IMOperation()
        if (debug) {
          operation.verbose()
        }
        // operation.define('stream:buffer-size=0') // We may need this to avoid cache problems
        operation.addImage(defaultConfiguration.srcFile.toString())
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
  
    private void imageMagickConvertImpl(WorkerExecutor workerExecutor, Class<? extends ImageMagickConvertOperation> actionClass, Stack<Provider<?>> configurationProviders) {
      workerExecutor.submit(actionClass, new Action<WorkerConfiguration>() {
        @Override
        void execute(WorkerConfiguration workerConfiguration) {
          workerConfiguration.isolationMode = IsolationMode.NONE

          Object[] allParams = new Object[configurationProviders.size() + 2]
          allParams[0] = (project.logging.level ?: project.gradle.startParameter.logLevel) <= LogLevel.DEBUG
          allParams[1] = outputDir.get().asFile
          for (int i = 0; i < configurationProviders.size(); i++) {
            // Processing from the back
            allParams[i + 2] = configurationProviders.pop().get()
          }
          workerConfiguration.params allParams
        }
      })
    }

    /**
     * Adds {@link ImageMagickConvertOperation} to specified {@link org.gradle.workers.WorkerExecutor}.
     * This should be called from method marked with {@link org.gradle.api.tasks.TaskAction}
     * in generator implementation.
     *
     * @param workerExecutor {@link org.gradle.workers.WorkerExecutor} which will run the operation
     * @param actionClass Actual operation class extending {@link ImageMagickConvertOperation}
     * @param params Params to pass to {@code actionClass} constructor
     */
    protected void imageMagickConvert(WorkerExecutor workerExecutor, Class<? extends ImageMagickConvertOperation> actionClass, Stack<Provider<? extends Object>> configurationProviders) {
      configurationProviders.push(defaultProperty)
      imageMagickConvertImpl workerExecutor, actionClass, configurationProviders
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
          workerConfiguration.params params
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

  @InheritConstructors
  abstract static class Converter extends AbstractConverter {
    /**
     * @inheritDocs
     */
    @Override
    protected final void imageMagickConvert(WorkerExecutor workerExecutor, Class<? extends ImageMagickConvertOperation> actionClass, Stack<Provider<? extends Object>> configurationProviders) {
      super.imageMagickConvert workerExecutor, actionClass, configurationProviders
    }
  }

  /**
   * Logo Generator that is able to generate a separate icon for RTL locale
   */
  @CompileStatic
  abstract private static class AbstractConverterWithRtl extends AbstractConverter {
    @DelegateWithGradleAnnotations(includeTypes = [ConfigurableRtl])
    private final RtlProvider rtlProvider
    {
      rtlProvider = new RtlPropertyImpl(project.objects, project.providers, project.layout)
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
      private final RtlImpl rtlConfiguration

      @Inject
      ImageMagickConvertOperation(boolean debug = false, File outputDir, DefaultImpl defaultConfiguration, RtlImpl rtlConfiguration) {
        super(debug, outputDir, defaultConfiguration)
        this.@rtlConfiguration = rtlConfiguration
      }
    }

    /**
     * @inheritDocs
     */

    protected void imageMagickConvert(WorkerExecutor workerExecutor, Class<? extends ImageMagickConvertOperation> actionClass, Stack<Provider<? extends Object>> configurationProviders) {
      configurationProviders.push(rtlProvider)
      super.imageMagickConvert(workerExecutor, actionClass, configurationProviders)
    }
  }

  @InheritConstructors
  abstract static class ConverterWithRtl extends AbstractConverterWithRtl {
    /**
     * @inheritDocs
     */
    @Override
    protected final void imageMagickConvert(WorkerExecutor workerExecutor, Class<? extends ImageMagickConvertOperation> actionClass, Stack<Provider<? extends Object>> configurationProviders) {
      super.imageMagickConvert workerExecutor, actionClass, configurationProviders
    }
  }

  /**
   * Logo Generator that is able to generate a separate icons for RTL and Hebrew locales
   */
  @CompileStatic
  abstract private static class AbstractConverterWithRtlAndHebrew extends AbstractConverterWithRtl {
    @DelegateWithGradleAnnotations(includeTypes = [ConfigurableHebrew])
    private final HebrewProvider hebrewProvider
    {
      hebrewProvider = new HebrewPropertyImpl(project.objects, project.providers, project.layout)
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
      private HebrewImpl hebrewConfiguration

      @Inject
      ImageMagickConvertOperation(boolean debug = false, File outputDir, DefaultImpl defaultConfiguration, RtlImpl rtlConfiguration, HebrewImpl hebrewConfiguration) {
        super(debug, outputDir, defaultConfiguration, rtlConfiguration)
        this.@hebrewConfiguration = hebrewConfiguration
      }
    }

    // TOTEST
    /**
     * @inheritDocs
     */
    protected void imageMagickConvert(WorkerExecutor workerExecutor, Class<? extends ImageMagickConvertOperation> actionClass, Stack<Provider<? extends Object>> configurationProviders) {
      configurationProviders.push(hebrewProvider)
      super.imageMagickConvert(workerExecutor, actionClass, configurationProviders)
    }
  }

  @InheritConstructors
  abstract static class ConverterWithRtlAndHebrew extends AbstractConverterWithRtlAndHebrew {
    /**
     * @inheritDocs
     */
    @Override
    protected final void imageMagickConvert(WorkerExecutor workerExecutor, Class<? extends ImageMagickConvertOperation> actionClass, Stack<Provider<? extends Object>> configurationProviders) {
      super.imageMagickConvert workerExecutor, actionClass, configurationProviders
    }
  }
}
