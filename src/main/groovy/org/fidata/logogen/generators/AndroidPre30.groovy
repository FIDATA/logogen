#!/usr/bin/env groovy
/*
 * Android ]1.5, 3.0[ Launcher Icon Generator
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

import com.google.common.collect.ImmutableMap
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import org.fidata.imagemagick.Units
import org.fidata.logogen.LogoGeneratorDescriptor
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import org.im4java.core.IMOperation
import javax.inject.Inject
import java.math.MathContext

/**
 * Android ]1.5, 3.0[ Launcher Icon
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
final class AndroidPre30 extends LogoGenerator {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('androidPre3.0', AndroidPre30)

  @InheritConstructors
  protected static class ImageMagickConvertOperation extends Android15.ImageMagickConvertOperation {
    protected static final Map<String, BigDecimal> DENSITY_FACTORS = ImmutableMap.copyOf([
      'ldpi':  0.75,
      'mdpi':  1.0,
      'hdpi':  1.5,
      'xhdpi': 2.0,  // Added in API level 8
    ])

    @Override
    protected IMOperation getOperation() {
      File resOutputDir = new File(super.outputDir, RES_DIR_NAME)

      IMOperation operation = new IMOperation()
      operation.background('none')

      DENSITY_FACTORS.each { String densityName, BigDecimal densityValue ->
        File densityOutputDir = new File(resOutputDir, "drawable-${densityName}")
        assert densityOutputDir.mkdirs()
        String outputFile = new File(densityOutputDir, 'ic_launcher.png').toString()

        int size = (densityValue * SIZE_DP).round(MathContext.UNLIMITED).intValueExact()

        operation.openOperation()
          .clone(0)
          .units(Units.PIXELSPERINCH.toString())
          .density((densityValue * DEF_DENSITY).round(MathContext.UNLIMITED).intValueExact())
          .resize(size, size)
          .write(outputFile)
          .delete()
        operation.closeOperation()
      }
      operation.delete('0--1')
      operation
    }
  }

  private final WorkerExecutor workerExecutor

  @Inject
  AndroidPre30(WorkerExecutor workerExecutor) {
    this.@workerExecutor = workerExecutor
  }

  @TaskAction
  protected void resizeAndConvert() {
    imageMagicConvert workerExecutor, ImageMagickConvertOperation, outputDir.get().asFile
  }
}
