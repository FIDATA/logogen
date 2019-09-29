// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import groovy.transform.CompileStatic
import org.fidata.imagemagick.Units
import org.fidata.logogen.annotations.DelegateWithGradleAnnotations
import org.fidata.logogen.generators.Generator
import org.fidata.logogen.shared.NameConfigurationProperty
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import org.im4java.core.IMOperation
import javax.inject.Inject

@CompileStatic
abstract class SimpleConverter extends Generator.AbstractConverter {
  private final WorkerExecutor workerExecutor
  private final int size
  private final String format
  private final Integer density

  @DelegateWithGradleAnnotations
  private final NameConfigurationProperty logoNameConfigurationProvider
  {
    logoNameConfigurationProvider = new NameConfigurationProperty(project.objects)
    logoName.convention(project.providers.provider {
      project.group.toString()
    })
  }

  final protected static class ImageMagickResizeAndConvertOperation extends Generator.AbstractConverter.ImageMagickConvertOperation {
    private final int size
    private final Integer density
    private final File outputFile

    @Inject
    ImageMagickResizeAndConvertOperation(File srcFile, boolean debug, File outputDir, int size, Integer density, String outputFileName) {
      super(srcFile, debug, outputDir)
      this.@size = size
      this.@density = density
      this.@outputFile = new File(super.outputDir, outputFileName)
    }

    @Override
    protected IMOperation getOperation() {
      IMOperation operation = new IMOperation()
      operation.background('none')
      if (density != null) {
        operation
          .units(Units.PIXELSPERINCH.toString())
          .density(density)
      }
      operation
        .resize(size, size)
        .write(outputFile.toString())
      operation
    }
  }

  SimpleConverter(WorkerExecutor workerExecutor, int size, String format, Integer density = null) {
    this.@workerExecutor = workerExecutor
    this.@size = size
    this.@format = format
    this.@density = density
  }

  @TaskAction
  protected final void resizeAndConvert() {
    imageMagickConvert workerExecutor, ImageMagickResizeAndConvertOperation,
      size,
      density,
      "${ logoName.get() }.$format".toString()
  }
}
