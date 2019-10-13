// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators

import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import groovy.transform.CompileStatic
import java.nio.file.Path
import java.nio.file.Paths
import javax.inject.Inject
import org.fidata.groovy.utils.InheritInjectableConstructors
import org.fidata.imagemagick.Units
import org.fidata.logogen.AbstractGenerator
import org.gradle.api.provider.Provider
import org.gradle.workers.WorkerExecutor
import org.im4java.core.IMOperation

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
final class FreeDesktop extends org.fidata.logogen.AbstractGenerator {
  @Override
  String getName() {
    'freeDesktop'
  }

  @Override
  Class<Converter> getConverterImplClass() {
    GitHubTask
  }

  @Override
  Class<?> getExtensionClass() { FreeDesktopExtension }

  @CompileStatic
  static final class Converter /* TODO */ extends AbstractGenerator.AbstractGeneratorTask<FreeDesktopConfiguration, Provider<FreeDesktopConfiguration>> {
    @Delegate(includeTypes = [ConfigurableFreeDesktopConfiguration], interfaces = false, methodAnnotations = true)
    protected final FreeDesktopConfigurationPropertyImpl configuration

    @Override
    protected Set<Class<? extends AbstractGenerator.AbstractGeneratorTask.IMConvertOperation<? extends FreeDesktopConfiguration>>> getIMConvertOperationClasses() {
      ImmutableSet.of(IMConvertOperation)
    }

    @Override
    protected Class<DefaultOutputLayout> getDefaultOutputLayoutClass() {
      DefaultOutputLayout
    }

    @Override
    protected Map<String, Class<? extends OutputLayout>> getAlternativeOutputLayoutClass() {
      ImmutableMap.of('gnome', (Class<? extends OutputLayout>)/* TODO: For IDEA ? */GnomeOutputLayout)
    }

    /*protected FreeDesktopExtension getProjectExtension() {
      getProjectExtension(FreeDesktopExtension)
    }*/

    @Inject
    Converter(WorkerExecutor workerExecutor) {
      super(workerExecutor)
      // TODO
      this.@configuration = new FreeDesktopConfigurationPropertyImpl(project.providers, project.objects, project.layout)
      logoName.convention project.providers.provider {
        project.group.toString()
      }
      // TODO sizes.convention projectExtension.sizes
    }

    @InheritInjectableConstructors
    private final static class IMConvertOperation extends AbstractGenerator.AbstractGeneratorTask.IMConvertOperation<FreeDesktopConfiguration> {
      // TODO
      @Override
      protected IMOperation getOperation() {
        IMOperation operation = new IMOperation()
        operation
          .background('none')
          .units(Units.PIXELSPERINCH.toString())
          .density(96)

        configuration.sizes.each { Integer size ->
          File outputFile = getFixedSizeOutputFile(super.outputDir, /*this.*/logoName, configuration, size)
          operation.openOperation()
            .clone(0)
            .resize(size, size)
            // TODO .write(outputFile.toString())
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

    private final static class DefaultOutputLayout implements AbstractGenerator.AbstractGeneratorTask.OutputLayout<FreeDesktopConfiguration> {
      @Override
      Path getOutputPath(FreeDesktopConfiguration configuration, Map<String, ?> context) {
        final Object size = context['size']
        String sizeDirName
        String extension
        if (size == 'scalable') {
          sizeDirName = size
          extension = 'svg'
        } else { // size instanceof Number
          sizeDirName = "${ size }x${ size }"
          extension = 'png'
        }
        Paths.get(configuration.theme, sizeDirName, 'apps', configuration.logoName, "${ configuration.logoName }.$extension")
      }
    }

    private final static class GnomeOutputLayout implements AbstractGenerator.AbstractGeneratorTask.OutputLayout<FreeDesktopConfiguration> {
      @Override
      Path getOutputPath(FreeDesktopConfiguration configuration, Map<String, ?> context) {
        final Object size = context['size']
        String sizeDirName
        String extension
        if (size == 'scalable') {
          sizeDirName = size
          extension = 'svg'
        } else { // size instanceof Number
          sizeDirName = "${ size }x${ size }"
          extension = 'png'
        }
        Paths.get(configuration.group /* TODO */, 'icons', sizeDirName, 'apps', "${ configuration.logoName }.$extension")
      }
    }
  }
}
