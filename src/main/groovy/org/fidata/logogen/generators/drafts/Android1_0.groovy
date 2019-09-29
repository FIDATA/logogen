// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import static org.fidata.android.AndroidUtils.*
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
final class Android1_0 extends Generator.AbstractConverter {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('android1.0', Android1_0, Classifiers.ANDROID, null)

  /**
   *
   */
  @InheritConstructors
  protected static class ImageMagickConvertOperation extends Generator.AbstractConverter.ImageMagickConvertOperation {
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
    imageMagickConvert workerExecutor, ImageMagickConvertOperation
  }
}
