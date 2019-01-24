package org.fidata.logogen.generators

import com.google.common.io.Resources
import groovy.text.Template
import groovy.text.TemplateEngine
import groovy.text.XmlTemplateEngine
import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.CopySpec
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.workers.IsolationMode
import org.gradle.workers.WorkerConfiguration
import org.gradle.workers.WorkerExecutor
import org.im4java.core.ConvertCmd
import org.im4java.core.IMOperation
import javax.inject.Inject
import org.gradle.api.tasks.TaskAction

/*
   name
     Name of generator. Used in names of tasks and output directory
   configureClosure
     Configuration closure
     Parameters:
       srcFile    Source file
       outputDir  Output directory
       debug      Boolean indicator of debug/verbose mode
     Return: array of maps with the following fields:
       type        (required)                  Class of task.
                                               Currently only generic
                                               Task, Exec and Copy
                                               classes are supported
       doLast      (required for type == Task) task action
       commandLine (required for type == Exec) command line for
                                               execution
       outputFiles (required for type == Exec) output files
       into        (required for type == Copy) output directory
       rename      (required for type == Copy) output file

   For filenames generators may use Gradle's project.group
     as name of application/project/organization
     logo is being created for
*/
@CacheableTask
@CompileStatic
abstract class LogoGenerator extends DefaultTask {
  @InputFile
  final RegularFileProperty srcFile = project.objects.fileProperty()

  @OutputDirectory
  final DirectoryProperty outputDir = project.objects.directoryProperty()

  /**
   * Class expressing ImageMagick {@code convert} operation
   *
   * <b>Constructor of class inheriting this one should have
   * {@code srcFile} and {@code debug} as first two arguments
   * and pass them to {@code super} constructor.
   * All other arguments may be placed after these two.</b>
   */
  abstract static class ImageMagickConvertOperation implements Runnable {
    private static final ConvertCmd CONVERT_CMD = new ConvertCmd()

    private final File srcFile
    private final boolean debug

    @Inject
    ImageMagickConvertOperation(File srcFile, boolean debug = false) {
      this.@srcFile = srcFile
      this.@debug = debug
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
   * Class expressing generation of XML file from existing template.
   * Template should be placed in resources, and implementation
   * should pass resource name as a first argument to {@code super} constructor
   */
  abstract static class GenerateXmlOperation implements Runnable {
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
   * Adds {@link ImageMagickConvertOperation} instance to specified {@link WorkerExecutor}.
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
        workerConfiguration.params(srcFile.get().asFile, (project.logging.level ?: project.gradle.startParameter.logLevel) <= LogLevel.DEBUG, *params)
      }
    })
  }

  /**
   * Adds {@link GenerateXmlOperation} instance to specified {@link WorkerExecutor}.
   * This should be called from method marked with {@link TaskAction}
   * in generator implementation.
   *
   * @param workerExecutor {@link WorkerExecutor} which will run the operation
   * @param actionClass Actual operation class extending {@link ImageMagickConvertOperation}
   * @param params Params to pass to {@code actionClass} constructor
   */
  protected void generateXml(WorkerExecutor workerExecutor, Class<? extends GenerateXmlOperation> actionClass, Object... params) {
    workerExecutor.submit(actionClass, new Action<WorkerConfiguration>() {
      @Override
      void execute(WorkerConfiguration workerConfiguration) {
        workerConfiguration.isolationMode = IsolationMode.NONE
        workerConfiguration.params(*params)
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
