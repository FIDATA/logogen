#!/usr/bin/env groovy
/*
 * freedesktop.org Application Icon Generator
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

import groovy.transform.CompileStatic
import org.fidata.imagemagick.Units
import org.fidata.logogen.annotations.DelegateWithGradleAnnotationsWithoutProviderInterface
import org.fidata.logogen.shared.Default
import org.fidata.logogen.shared.NameConfigurationProvider
import org.gradle.api.file.CopySpec
import org.gradle.api.provider.Provider
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
class FreeDesktopGenerator extends Generator {
  public static final String NAME = 'freeDesktop'

  @Override
  String getName() { NAME }

  @Override
  Class<? extends Generator.Converter> getConverterImplementationClass() { Converter }

  private static final File getOutputFile(File outputDir, String logoName, FreeDesktopConfiguration configuration, String sizeDirName, String extension) {
    new File(outputDir, "$configuration.theme/$sizeDirName/apps/${ logoName }.$extension")
  }

  static final File getFixedSizeOutputFile(File outputDir, String logoName, FreeDesktopConfiguration configuration, int size) {
    getOutputFile(outputDir, logoName,  configuration, "${ size }x${ size }", 'png')
  }

  static final File getScalableOutputFile(File outputDir, String logoName, FreeDesktopConfiguration configuration) {
    getOutputFile(outputDir, logoName,  configuration, 'scalable', 'svg')
  }

  @Override
  Map<String, List<CopySpec>> getOutputs(File outputDir, String logoName, Provider<? extends Default> configuration) {
    [
      (null): [copySpec { CopySpec copySpec1 ->
        copySpec1.from outputDir
      }],
      'gnome': { ->
        List<CopySpec> copySpecs
        ((Provider<FreeDesktopConfiguration>)configuration).get().sizes.each { Integer size ->
          copySpecs.add copySpec { CopySpec copySpec ->
            copySpec.from getFixedSizeOutputFile(outputDir, logoName, (FreeDesktopConfiguration)configuration.get(), size) // TODO
            copySpec.into file("org/my/App/icons/${ size }x${ size}/apps")
          }
        }
        copySpecs.add copySpec { CopySpec copySpec ->
          copySpec.from getScalableOutputFile(outputDir, logoName, (FreeDesktopConfiguration)configuration.get()) // TODO
          copySpec.into file("$group/$appName/icons/scalable/apps")
        }
        copySpecs
      }.call()
    ]
  }

  @Override
  Class<?> getExtensionClass() { FreeDesktopExtension }

  @CompileStatic
  static final class Converter extends Generator.Converter {
    protected FreeDesktopExtension getProjectExtension() {
      getProjectExtension(FreeDesktopExtension)
    }

    @Delegate(methodAnnotations = true)
    private final NameConfigurationProvider logoNameConfigurationProvider
    {
      logoNameConfigurationProvider = new NameConfigurationProvider(project.objects)
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

    protected final static class ImageMagickConvertOperation extends Generator.Converter.ImageMagickConvertOperation {
      private final String logoName
      private final FreeDesktopConfiguration configuration

      ImageMagickConvertOperation(File srcFile, boolean debug, File outputDir, String logoName, FreeDesktopConfiguration configuration) {
        super(srcFile, debug, outputDir)
        this.@logoName = logoName
        this.@configuration = configuration
      }

      @Override
      protected IMOperation getOperation() {
        IMOperation operation = new IMOperation()
        operation
          .background('none')
          .units(Units.PIXELSPERINCH.toString())
          .density(96)

        configuration.sizes.each { Integer size ->
          File outputFile = getFixedSizeOutputFile(super.outputDir, /*this.*/logoName, configuration, size)
          assert outputFile.parentFile.mkdirs()
          operation.openOperation()
            .clone(0)
            .resize(size, size)
            .write(outputFile.toString())
            // TOTEST: delete
          operation.closeOperation()
        }

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
    Converter(WorkerExecutor workerExecutor) {
      this.@workerExecutor = workerExecutor
    }

    @TaskAction
    protected void resizeAndConvert() {
      imageMagicConvert workerExecutor, ImageMagickConvertOperation,
        logoName.get(),
        freeDesktopConfigurationProvider.get()
    }
  }
}
