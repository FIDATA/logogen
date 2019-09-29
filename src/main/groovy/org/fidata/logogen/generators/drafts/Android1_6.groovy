// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import org.fidata.logogen.annotations.DelegateWithGradleAnnotationsWithoutProviderInterface

import static org.fidata.android.AndroidUtils.*
import org.fidata.android.DensityFactor
import groovy.transform.CompileStatic
import org.fidata.imagemagick.Units
import org.fidata.logogen.LogoGeneratorDescriptor
import org.fidata.logogen.generators.Generator
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import org.im4java.core.IMOperation
import javax.inject.Inject
import java.math.MathContext

/**
 * Android ]1.5, 4.3[ Launcher Icon
 *
 * File format: PNG (preferred)/JPG (acceptable)/GIF (discouraged)
 * Directory/file layout: res/drawable-{density}/ic_launcher.png
 * Size: 48×48 dp
 * Default density: 160
 * Density factors: 0.75 (lpi), 1.0 (mdpi, default), 1.5 (hdpi), 2.0 (xhdpi)
 *
 * References:
 * 1. Providing alternative resources // App resources overview
 *    https://developer.android.com/guide/topics/resources/providing-resources#AlternativeResources
 * 2. Supporting Different Densities
 *    https://developer.android.com/training/multiscreen/screendensities#mipmap
 * 3. Use Assets Designed for Tablet Screens // Tablet App Quality
 *    http://developer.android.com/distribute/essentials/quality/tablets.html#use-tablet-icons
 * 4. https://sites.google.com/site/yourscorpion2/home/edinicy-izmerenia-v-ios-i-android
 *
 * Notes:
 * 1. This generator doesn't respect Material Design guidelines specified at
 *    * https://material.io/design/iconography/product-icons.html
 *    * https://material.io/design/platform-guidance/android-icons.html
 * 2. This generator doesn't create any special padding, as proposed at
 *    https://android-developers.googleblog.com/2013/07/making-beautiful-android-app-icons.html
 */
@CompileStatic
final class Android1_6 extends Generator.AbstractConverter /* TODO: ConverterWithRtlAndHebrew*/ {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('android1.6', Android1_6, Classifiers.ANDROID, Android1_6Extension)

  protected Android1_6Extension getProjectExtension() {
    getProjectExtension(Android1_6Extension)
  }

  @DelegateWithGradleAnnotationsWithoutProviderInterface
  private final AndroidConfigurationProviderImpl androidConfigurationProvider
  {
    androidConfigurationProvider = new AndroidConfigurationProviderImpl(project.providers, project.objects)
    densityFactors.convention projectExtension.densityFactors
  }

  protected static class ImageMagickConvertOperation extends Android1_0.ImageMagickConvertOperation {
    private final AndroidConfiguration configuration

    ImageMagickConvertOperation(File srcFile, boolean debug = false, File outputDir, AndroidConfiguration configuration) {
      super(srcFile, debug, outputDir)
      this.@configuration = configuration
    }

    @Override
    protected IMOperation getOperation() {
      File resOutputDir = new File(super.outputDir, RES_DIR_NAME)

      IMOperation operation = new IMOperation()
      operation.background('none')

      configuration.densityFactors.each { DensityFactor densityFactor ->
        File densityOutputDir = new File(resOutputDir, "drawable-${densityFactor.name}")
        assert densityOutputDir.mkdirs()
        File outputFile = new File(densityOutputDir, 'ic_launcher.png')

        int size = (densityFactor.factor * SIZE_DP).round(MathContext.UNLIMITED).intValueExact()

        operation.openOperation()
          .clone(0)
          .units(Units.PIXELSPERINCH.toString())
          .density((densityFactor.factor * DEF_DENSITY).round(MathContext.UNLIMITED).intValueExact())
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
  Android1_6(WorkerExecutor workerExecutor) {
    this.@workerExecutor = workerExecutor
  }

  @TaskAction
  protected void resizeAndConvert() {
    imageMagickConvert workerExecutor, ImageMagickConvertOperation,
      androidConfigurationProvider.get()
  }
}
