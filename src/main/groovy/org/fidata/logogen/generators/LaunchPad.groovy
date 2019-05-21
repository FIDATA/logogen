#!/usr/bin/env groovy
/*
 * LaunchPad Team Images Generator
 * Copyright ©  Basil Peace
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

import com.google.common.collect.ImmutableMap
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import org.fidata.imagemagick.Units
import org.fidata.logogen.LogoGeneratorDescriptor
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import org.im4java.core.IMOperation
import javax.inject.Inject

/**
 * LaunchPad Team Images
 *
 * File format: ?
 * Sizes: 14 (Icon), 64 (Logo), 192 (Mugshot)
 * Maximum file sizes: <= 5 KB (Icon), <= 50 KB (Logo), <= 100 KB (Mugshot)
 *
 * References:
 * 1. https://launchpad.net/~<your-organization>/+branding
 */
@CompileStatic
final class LaunchPad extends Generator.Converter {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('launchpad', LaunchPad, 'launchpad', null)

  @InheritConstructors
  protected static class ImageMagickConvertOperation extends Generator.Converter.ImageMagickConvertOperation {
    public static final Map<String, Integer> SIZES = ImmutableMap.of(
      'Icon',    14,
      'Logo',    64,
      'Mugshot', 192,
    )

    @Override
    protected IMOperation getOperation() {
      IMOperation operation = new IMOperation()
      operation.background('none')
        .units(Units.PIXELSPERINCH.toString())

      SIZES.each { String name, Integer size ->
        File outputFile = new File(super.outputDir, "${ name }.png")

        operation.openOperation()
          .clone(0)
          .resize(size, size)
          .write(outputFile.toString())
          .delete()
        operation.closeOperation()
      }
      operation.delete('0--1')
      operation
    }
  }

  private final WorkerExecutor workerExecutor

  @Inject
  LaunchPad(WorkerExecutor workerExecutor) {
    this.@workerExecutor = workerExecutor
  }

  @TaskAction
  protected void resizeAndConvert() {
    imageMagicConvert workerExecutor, ImageMagickConvertOperation
  }
}
