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

import groovy.transform.CompileStatic
import org.fidata.imagemagick.Compress
import org.fidata.imagemagick.Units
import org.fidata.logogen.LogoGenExtension
import org.fidata.logogen.LogoGeneratorDescriptor
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.util.ConfigureUtil
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

  @Input
  final MapProperty<Integer, WindowsMainIconExtension.ColorDepth> depths = project.objects.mapProperty(Integer, WindowsMainIconExtension.ColorDepth).convention(
    ((ExtensionAware)project.extensions.findByType(LogoGenExtension))?.extensions?.getByType(WindowsMainIconExtension)?.depths
  )

  @Input
  final ListProperty<Integer> sizes = project.objects.listProperty(Integer).empty()

  void depth(int depth, @DelegatesTo(WindowsMainIconExtension.ColorDepth) Closure configureClosure) {
    depths.put depth, project.providers.provider {
      WindowsMainIconExtension.ColorDepth colorDepth = new WindowsMainIconExtension.ColorDepth(project.objects, sizes)
      ConfigureUtil.configure configureClosure, colorDepth
    }
  }

  @Input
  final Property<Compress> compress = project.objects.property(Compress).convention(
    ((ExtensionAware)project.extensions.findByType(LogoGenExtension))?.extensions?.getByType(WindowsMainIconExtension)?.compress
  )

  protected final static class ImageMagickConvertOperation extends LogoGenerator.ImageMagickConvertOperation {
    public static final String MAINICON_ICO_FILE_NAME = 'MAINICON.ico'

    private final WindowsMainIconConfiguration configuration

    ImageMagickConvertOperation(File srcFile, boolean debug, File outputDir, WindowsMainIconConfiguration configuration) {
      super(srcFile, debug, outputDir)
      this.@configuration = configuration
    }

    @Override
    protected IMOperation getOperation() {
      IMOperation operation = new IMOperation()
      operation
        .background('none')
        .units(Units.PIXELSPERINCH.toString())

      File outputFile = new File(super.outputDir, MAINICON_ICO_FILE_NAME)

      configuration.depths.each { Integer depth, WindowsMainIconConfiguration.ColorDepth colorDepth ->
        IMOperation colorReductionOperation = colorDepth.colorReduction?.toIMOperation()
        for (Integer size in colorDepth.sizes) {
          operation.openOperation()
            .clone(0)
          if (colorReductionOperation != null) {
            operation.addOperation(colorReductionOperation)
          }
          if (depth != null) { // TODO
            operation.colors(2**depth)
          }
          operation.resize(size, size)
          if (depth != null) { // TODO
            operation.depth(depth)
          }
          operation.closeOperation()
        }
      }

      operation
        .delete(0)
        .compress(configuration.compress.toString())
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
    imageMagicConvert workerExecutor, ImageMagickConvertOperation, new WindowsMainIconConfiguration(
      /*(Map<Integer, WindowsMainIconConfiguration.ColorDepth>)*/depths.get().collectEntries { Integer depth, WindowsMainIconExtension.ColorDepth colorDepth ->
        [(depth): new WindowsMainIconConfiguration.ColorDepth(colorDepth.sizes.get(), colorDepth.reduction.getOrNull())]
      },
      compress.get()
    )
  }
}
