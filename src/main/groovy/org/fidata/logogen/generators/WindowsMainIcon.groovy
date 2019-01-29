#!/usr/bin/env groovy
/*
 * Windows Application Icon (MAINICON) Generator
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

import com.google.common.collect.ImmutableList
import groovy.transform.CompileStatic
import org.fidata.imagemagick.Units
import org.fidata.imagemagick.quantization.color.ColorReduction
import org.fidata.imagemagick.quantization.color.UniformColors
import org.fidata.logogen.LogoGeneratorDescriptor
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import org.im4java.core.IMOperation
import javax.inject.Inject

/**
 * Windows Application Icon (MAINICON)
 *
 * File format: ICO
 *   Windows before Vista (XP) doesn't support compressed icons (PNG inside ICO) [5]
 * Directory/file layout: all icons in one multipage MAINICON resource
 *   of executable file. Recommended sort order: by color depth ASC,
 *   then by size DESC
 * Density-independent sizes in px: 256 (Windows Vista+)
 * Sizes in dp: 16, 32, 48
 * Default density: 96
 * Density factors: 1.0 to 5.0
 *   Most common: 1.0, 1.25, 1.5, 2.0
 * Complete set of sizes for most common DPIs: 256, 96, 72, 64, 60, 48, 40, 32, 24,
 *   20, 16
 * Color depth: 4-, 8-, 32-bit
 *
 * References:
 * 1. Icons
 *    https://msdn.microsoft.com/en-us/library/dn742485.aspx
 * 2. Creating Windows XP Icons
 *    https://msdn.microsoft.com/en-us/library/ms997636.aspx
 * 3. http://wiki.lazarus.freepascal.org/Windows_Icon
 * 4. http://www.hhhh.org/cloister/csharp/icons/
 * 5. Setting Window.Icon to Vista 256x256 PNG icon causes a FileFormatException
 *    https://social.msdn.microsoft.com/Forums/vstudio/en-US/9e723d51-0e26-4609-8fe1-5e0edcf7385b/setting-windowicon-to-vista-256x256-png-icon-causes-a-fileformatexception?forum=wpf
 * 6. Windows 10 and 768x768 thumbnail size? // Windows TenForums
 *    https://www.tenforums.com/general-support/11711-windows-10-768x768-thumbnail-size.html
 * 7. Notes on .ICO files // UIconEdit
 *    https://github.com/KimikoMuffin/UIconEdit/blob/master/Notes%20on%20ICO%20Files.md
 *
 * Notes:
 * 1. This generator doesn't respect Windows Icons guidelines described at
 *    https://docs.microsoft.com/en-us/windows/desktop/uxguide/vis-icons#guidelines
 * 2. As noted at [6] and [7], some Windows 10 developer preview builds (Project Threshold)
 *    had 512×512 and 768x768 icons.
 *    This feature wasn't promoted to released version.
 *    This generator doesn't generate icons of these sizes.
 *    Also, note that Windows uses only one icon of size larger than 255 px.
 * 3. This generator doesn't compress 256×256 icon. Compressed icons are not supported by Windows before Vista
 * 4. This generator doesn't generate icons of additional sizes (128, 22, 14, 10, 8)
 *    used for annotations, toolbar strips, overlays. Application MAINICON is not expected to be used here. See also
 *    a note in the end of this article: https://www.creativefreedom.co.uk/icon-designers-blog/windows-7-icon-sizes/
 */
@CompileStatic
final class WindowsMainIcon extends LogoGenerator {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('windowsMainIcon', WindowsMainIcon, WindowsMainIconExtension)

  @Nested
  // These settings may be not the best for some logos.
  // TODOC
  public final Property<ColorReduction> colorDepth4Reduction = project.objects.property(ColorReduction).convention(new UniformColors(3))
  @Nested
  public final Property<ColorReduction> colorDepth8Reduction = project.objects.property(ColorReduction)

  protected final static class ImageMagickConvertOperation extends LogoGenerator.ImageMagickConvertOperation {
    private static final List<Integer> SIZES = ImmutableList.copyOf([256, 96, 72, 64, 60, 48, 40, 32, 24, 20, 16])

    public static final String MAINICON_ICO_FILE_NAME = 'MAINICON.ico'

    private final ColorReduction colorDepth4Reduction
    private final ColorReduction colorDepth8Reduction

    ImageMagickConvertOperation(File srcFile, boolean debug, File outputDir, ColorReduction colorDepth4Reduction, ColorReduction colorDepth8Reduction) {
      super(srcFile, debug, outputDir)
      this.@colorDepth4Reduction = colorDepth4Reduction
      this.@colorDepth8Reduction = colorDepth8Reduction
    }

    @Override
    protected IMOperation getOperation() {
      IMOperation operation = new IMOperation()
      operation
        .background('none')
        .units(Units.PIXELSPERINCH.toString())

      File outputFile = new File(super.outputDir, MAINICON_ICO_FILE_NAME)

      Map<Integer, IMOperation> colorDepthReductionOperations = [
        4: colorDepth4Reduction.toIMOperation(),
        8: colorDepth8Reduction.toIMOperation(),
      ]

      colorDepthReductionOperations.each { Integer colorDepth, IMOperation colorReductionOperation ->
        for (Integer size in SIZES) {
          operation.openOperation()
            .clone(0)
          if (colorReductionOperation != null) {
            operation.addOperation(colorReductionOperation)
          }
          operation
            .colors(2 ** colorDepth)
            .resize(size, size)
            .depth(4)
          operation.closeOperation()
        }
      }

      for (Integer size in SIZES) {
        operation.openOperation()
          .clone(0)
          .resize(size, size)
        operation.closeOperation()
      }

      operation
        .delete(0)
        .compress('None')
        .write(outputFile.toString())

      operation
    }
  }

  private final WorkerExecutor workerExecutor

  @Inject
  WindowsMainIcon(WorkerExecutor workerExecutor) {
    this.@workerExecutor = workerExecutor
  }

  @TaskAction
  protected void resizeAndConvert() {
    imageMagicConvert workerExecutor, ImageMagickConvertOperation, colorDepth4Reduction.orNull, colorDepth8Reduction.orNull

    project.provid
  }
}
