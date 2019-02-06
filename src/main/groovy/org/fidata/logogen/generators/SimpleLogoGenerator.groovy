#!/usr/bin/env groovy
/*
 * SimpleLogoGenerator Gradle task class
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

import groovy.transform.CompileStatic
import org.fidata.imagemagick.Units
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import org.im4java.core.IMOperation
import javax.inject.Inject

@CompileStatic
abstract class SimpleLogoGenerator extends LogoGenerator {
  private final WorkerExecutor workerExecutor
  private final int size
  private final String format
  private final Integer density

  final protected static class ImageMagickResizeAndConvertOperation extends LogoGenerator.ImageMagickConvertOperation {
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

  SimpleLogoGenerator(WorkerExecutor workerExecutor, int size, String format, Integer density = null) {
    this.@workerExecutor = workerExecutor
    this.@size = size
    this.@format = format
    this.@density = density
  }

  @TaskAction
  protected final void resizeAndConvert() {
    imageMagicConvert workerExecutor, ImageMagickResizeAndConvertOperation, size, density, "${ project.group }.$format".toString()
  }
}
