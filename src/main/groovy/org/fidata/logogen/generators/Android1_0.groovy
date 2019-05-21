#!/usr/bin/env groovy
/*
 * Android <= 1.5 Launcher Icon Generator
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

import static org.fidata.android.AndroidUtils.*
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import org.fidata.imagemagick.Units
import org.fidata.logogen.LogoGeneratorDescriptor
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import org.im4java.core.IMOperation
import javax.inject.Inject

/**
 * Android <= 1.5 Launcher Icon
 *
 * File format: PNG (preferred)/JPG (acceptable)/GIF (discouraged)
 * Directory/file layout: res/drawable/ic_launcher.png
 * Size: 48×48 dp
 * Density: 160
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
final class Android1_0 extends Generator.Converter {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('android1.0', Android1_0, Classifiers.ANDROID, null)

  /**
   *
   */
  @InheritConstructors
  protected static class ImageMagickConvertOperation extends Generator.Converter.ImageMagickConvertOperation {
    protected static final BigDecimal SIZE_DP = 48
    protected static final BigDecimal DEF_DENSITY = 160

    @Override
    protected IMOperation getOperation() {
      File drawableOutputDir = super.outputDir.toPath().resolve(RES_DIR_NAME).resolve('drawable').toFile()
      assert drawableOutputDir.mkdirs()
      File outputFile = new File(drawableOutputDir, IC_LAUNCHER_FILE_NAME)

      int size = SIZE_DP.intValueExact()

      IMOperation operation = new IMOperation()
      operation
        .background('none')
        .clone(0)
        .units(Units.PIXELSPERINCH.toString())
        .density(DEF_DENSITY.intValueExact())
        .resize(size, size)
        .write(outputFile.toString())
      operation
    }
  }

  private final WorkerExecutor workerExecutor

  @Inject
  Android1_0(WorkerExecutor workerExecutor) {
    this.@workerExecutor = workerExecutor
  }

  @TaskAction
  protected void resizeAndConvert() {
    imageMagicConvert workerExecutor, ImageMagickConvertOperation
  }
}
