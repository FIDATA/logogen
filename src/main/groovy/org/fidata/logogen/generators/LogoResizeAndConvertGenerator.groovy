package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.IsolationMode
import org.gradle.workers.WorkerConfiguration
import org.gradle.workers.WorkerExecutor
import org.im4java.core.IMOperation
import javax.inject.Inject

@CompileStatic
abstract class LogoResizeAndConvertGenerator extends LogoGenerator {
  private final WorkerExecutor workerExecutor
  private final int size
  private final String format
  private final Integer density

  final static class ImageMagickResizeAndConvertRunnable extends LogoGenerator.ImageMagickConvertRunnable {
    private final int size
    private final String format
    private final Integer density

    @Inject
    ImageMagickResizeAndConvertRunnable(String srcFile, boolean debug, int size, String format, Integer density) {
      super(srcFile, debug)
      this.@size = size
      this.@format = format
      this.@density = density
    }

    @Override
    protected void configureOperation(IMOperation operation) {
      operation.background('none')
      if (density != null) {
        operation.units('pixelsperinch')
        operation.density(density)
      }
      operation.resize(size, size)
      operation.addImage(outputFile) // TODO
    }
  }

  LogoResizeAndConvertGenerator(WorkerExecutor workerExecutor, int size, String format, Integer density = null) {
    this.@workerExecutor = workerExecutor
    this.@size = size
    this.@format = format
    this.@density = density
  }

  @TaskAction
  protected final void resizeAndConvert() {
    workerExecutor.submit(ImageMagickResizeAndConvertRunnable, new Action<WorkerConfiguration>() {
      @Override
      void execute(WorkerConfiguration workerConfiguration) {
        workerConfiguration.isolationMode = IsolationMode.NONE
        workerConfiguration.params(srcFile, (project.logging.level ?: project.gradle.startParameter.logLevel) <= LogLevel.DEBUG, size, format, density)
      }
    })
  }
}
