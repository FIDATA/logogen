package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import org.im4java.core.IMOperation
import javax.inject.Inject

@CompileStatic
abstract class LogoResizeAndConvertGenerator extends LogoGenerator {
  private final WorkerExecutor workerExecutor
  private final int size
  private final String format
  private final Integer density

  final static class ImageMagickResizeAndConvertOperation extends LogoGenerator.ImageMagickConvertOperation {
    private final int size
    private final String format
    private final Integer density

    @Inject
    ImageMagickResizeAndConvertOperation(String srcFile, boolean debug, int size, String format, Integer density) {
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
    imageMagicConvert(workerExecutor, ImageMagickResizeAndConvertOperation, size, format, density)
  }
}
