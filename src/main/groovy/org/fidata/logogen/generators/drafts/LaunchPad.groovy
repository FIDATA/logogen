// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import com.google.common.collect.ImmutableMap
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import org.fidata.imagemagick.Units
import org.fidata.logogen.LogoGeneratorDescriptor
import org.fidata.logogen.generators.Generator
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
final class LaunchPad extends Generator.AbstractConverter {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('launchpad', LaunchPad, 'launchpad', null)

  @InheritConstructors
  protected static class ImageMagickConvertOperation extends Generator.AbstractConverter.ImageMagickConvertOperation {
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
    imageMagickConvert workerExecutor, ImageMagickConvertOperation
  }
}
