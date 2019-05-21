#!/usr/bin/env groovy
/*
 * Android 3.0+ Launcher Icon Generator
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

import org.fidata.logogen.annotations.DelegateWithGradleAnnotationsWithoutProviderInterface

import static org.fidata.android.AndroidUtils.*
import org.fidata.android.DensityFactor
import groovy.transform.CompileStatic
import org.fidata.imagemagick.Units
import org.fidata.logogen.LogoGeneratorDescriptor
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import org.im4java.core.IMOperation
import javax.inject.Inject
import java.math.MathContext

/**
 * Android 4.3+ Launcher Icon
 *
 * File format: PNG (preferred)/JPG (acceptable)/GIF (discouraged)
 * Directory/file layout: res/mipmap-{density}/ic_launcher.png
 * Size: 48×48 dp
 * Default density: 160
 * Density factors: 0.75 (lpi), 1.0 (mdpi, default), 1.33 (tvdpi),
 *   1.5 (hdpi), 2.0 (xhdpi), 3.0 (xxhdpi), 4.0 (xxxhpdi)
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
 * 3. This generator doesn't use SVG as Vector Drawable image supported since Android 5.0 (API level 21)
 *    that is described at https://developer.android.com/guide/topics/graphics/vector-drawable-resources
 * 4. This generator doesn't create Adaptive icons that were added in Android 8.0 (API level 26) and are described at
 *    https://developer.android.com/guide/practices/ui_guidelines/icon_design_adaptive
 */
@CompileStatic
final class Android4_3 extends Generator.ConverterWithRtlAndHebrew {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('android4.3', Android4_3, Classifiers.ANDROID, Android4_3Extension)

  protected Android4_3Extension getProjectExtension() {
    getProjectExtension(Android4_3Extension)
  }

  @DelegateWithGradleAnnotationsWithoutProviderInterface
  private final AndroidConfigurationProviderImpl androidConfigurationProvider
  {
    androidConfigurationProvider = new AndroidConfigurationProviderImpl(project.providers, project.objects)
    densityFactors.convention projectExtension.densityFactors
  }

  protected final static class ImageMagickConvertOperation extends Android1_6.ImageMagickConvertOperation /* TODO */ {
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
        File densityOutputDir = new File(resOutputDir, "mipmap-$densityFactor.name")
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
  Android4_3(WorkerExecutor workerExecutor) {
    this.@workerExecutor = workerExecutor
  }

  @TaskAction
  protected void resizeAndConvert() {
    imageMagicConvert workerExecutor, ImageMagickConvertOperation,
      androidConfigurationProvider.get()
  }
}
