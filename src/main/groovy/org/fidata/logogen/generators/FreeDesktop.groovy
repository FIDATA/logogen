#!/usr/bin/env groovy
/*
 * freedesktop.org Application Icon Generator
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
import org.fidata.logogen.LogoGeneratorDescriptor
import org.fidata.logogen.annotations.DelegateWithGradleAnnotationsWithoutProviderInterface
import org.fidata.logogen.shared.LogoNameConfigurationProvider
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import org.im4java.core.IMOperation
import javax.inject.Inject

/*
   Free Desktop

   File format: PNG (required)/XPM (supported for backward
     compatibility)
   Directory/file layout:
     * {theme}/{size}x{size}/apps/{appname}.png
     * {theme}/{size}x{size}/apps/{appname}.icon - in INI format
       (optional, isn't generated here)
   Default theme: hicolor (required)
   Sizes: 16, 22, 24, 32, 36, 48, 64, 72, 96, 128, 192, 256, 512
   Default density: 96

   File format: SVG
   Directory/file layout:
     * {theme}/scalable/apps/{appname}.png
     * {theme}/scalable/apps/{appname}.icon - in INI format
       (optional, isn't generated here)
   Default theme: hicolor (required)

   References:
   1. Icon Theme Specification
      http://standards.freedesktop.org/icon-theme-spec/icon-theme-spec-latest.html
   2. xdg/default-icon-theme
      http://cgit.freedesktop.org/xdg/default-icon-theme/tree/index.theme
   3. Themed Icons
      https://developer.gnome.org/ThemedIcons/
   4. Tango Icon Theme Guidelines
      http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines
*/
@CompileStatic
final class FreeDesktop extends Converter {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('freeDesktop', FreeDesktop, 'freedesktop', FreeDesktopExtension)

  protected FreeDesktopExtension getProjectExtension() {
    getProjectExtension(FreeDesktopExtension)
  }

  @Delegate(methodAnnotations = true)
  private final LogoNameConfigurationProvider logoNameConfigurationProvider
  {
    logoNameConfigurationProvider = new LogoNameConfigurationProvider(project.objects)
    logoName.convention(project.providers.provider {
      project.group.toString()
    })
  }

  @DelegateWithGradleAnnotationsWithoutProviderInterface
  private final FreeDesktopConfigurationProviderImpl freeDesktopConfigurationProvider
  {
    freeDesktopConfigurationProvider = new FreeDesktopConfigurationProviderImpl(project.providers, project.objects)
    sizes.convention projectExtension.sizes
  }

  protected final static class ImageMagickConvertOperation extends Converter.ImageMagickConvertOperation {
    private final String logoName
    private final FreeDesktopConfiguration configuration

    ImageMagickConvertOperation(File srcFile, boolean debug, File outputDir, String logoName, FreeDesktopConfiguration configuration) {
      super(srcFile, debug, outputDir)
      this.@configuration = configuration
    }

    @Override
    protected IMOperation getOperation() {
      IMOperation operation = new IMOperation()
      operation
        .background('none')
        .units(Units.PIXELSPERINCH.toString())
        .density(96)

      File outputSubdir = new File(super.outputDir, configuration.theme)

      configuration.sizes.each { Integer size ->
        File outputFile = new File(outputSubdir, "${ size }x${ size }/apps/${ logoName }.png"))
        operation.openOperation()
          .clone(0)
          .resize(size, size)
          .write(outputFile.toString())
        operation.closeOperation()
      }

      operation
        .write(new File(outputSubdir, "scalable/apps/${ logoName }.svg").toString())
        // .delete(0)

      /* TODO: run svgo
      args = [
        'svgo',
        srcFile,
        '--config=' + file("$includePath/freedesktop.svgo.config.json"),
        outputFile
      ]*/

      operation
    }
  }

  private final WorkerExecutor workerExecutor

  @Inject
  FreeDesktop(WorkerExecutor workerExecutor) {
    this.@workerExecutor = workerExecutor
  }

  @TaskAction
  protected void resizeAndConvert() {
    imageMagicConvert workerExecutor, ImageMagickConvertOperation,
      logoName.get(),
      freeDesktopConfigurationProvider.get()
  }
}
