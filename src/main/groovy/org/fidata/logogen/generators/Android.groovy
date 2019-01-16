#!/usr/bin/env groovy
import com.google.common.collect.ImmutableMap
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import org.fidata.logogen.generators.LogoGenerator
import org.gradle.api.Action
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.IsolationMode
import org.gradle.workers.WorkerConfiguration
import org.gradle.workers.WorkerExecutor
import org.im4java.core.IMOperation

import javax.inject.Inject
import java.math.MathContext

/*
 * Android Launcher Icon Generator
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
/*
   Android Launcher Icon

   File format: PNG/JPG/GIF
   Directory/file layout:
     Android 3.0+: res/mipmap-{density}/ic_launcher.png
     Android before 3.0: res/drawable-{density}/ic_launcher.png [7]
     Android 1.5: res/drawable/ic_launcher.png [8]
   Sizes in dp: 48
   Default density: 160
   Density factors: 0.75 (lpi), 1.0 (mdpi, default), 1.33 (tvdpi),
     1.5 (hdpi), 2.0 (xhdpi), 3.0 (xxhdpi), 4.0 (xxxhpdi)
     Android 1.5 doesn't support density factors (i.e. mdpi only) [8]

   References:
   1. Use Assets Designed for Tablet Screens // Tablet App Quality
      http://developer.android.com/distribute/essentials/quality/tablets.html#use-tablet-icons
   2. Supporting Different Densities
      http://developer.android.com/training/multiscreen/screendensities.html
   3. Managing Launcher Icons as mipmap Resources
      http://developer.android.com/tools/projects/index.html#mipmap
   4. Icons
      https://www.google.com/design/spec/style/icons.html
   5. http://www.androiddesign.tips/
   6. http://android-developers.blogspot.ru/2013/07/making-beautiful-android-app-icons.html
   7. http://developer.android.com/reference/android/R.mipmap.html
   8. http://tekeye.biz/2013/android-icon-size
*/
@CompileStatic
final class Android extends LogoGenerator {
  private final WorkerExecutor workerExecutor
  private final int size
  private final String format
  private final Integer density

  @InheritConstructors
  final static class ImageMagickAndroidRunnable extends LogoGenerator.ImageMagickConvertRunnable {
    private static final BigDecimal SIZE_DP = 48
    private static final BigDecimal DEF_DENSITY = 160
    private static final Map<String, BigDecimal> DENSITY_FACTORS = ImmutableMap.copyOf([
      'ldpi':    0.75,
      'mdpi':    1.0,
      'tvdpi':   1.33,
      'hdpi':    1.5,
      'xhdpi':   2.0,
      'xxhdpi':  3.0,
      'xxxhdpi': 4.0,
    ])
    @Override
    protected void configureOperation(IMOperation operation) {
      operation.background('none')
      DENSITY_FACTORS.each { String densityName, BigDecimal densityValue ->
        String outputFile = "$outputDir/res/mipmap-${densityName}/ic_launcher.png" // TODO
        int size = (densityValue * SIZE_DP).round(MathContext.UNLIMITED).intValueExact()
        operation.openOperation()
        operation.units('pixelsperinch')
        operation.density((densityValue * DEF_DENSITY).round(MathContext.UNLIMITED).intValueExact())
        operation.resize(size, size)
        operation.addImage(outputFile)
        operation.closeOperation()
      }
    }
  }

  @Inject
  Android(WorkerExecutor workerExecutor) {
    this.@workerExecutor = workerExecutor
  }

  @TaskAction
  protected final void resizeAndConvert() {
    workerExecutor.submit(ImageMagickAndroidRunnable, new Action<WorkerConfiguration>() {
      @Override
      void execute(WorkerConfiguration workerConfiguration) {
        workerConfiguration.isolationMode = IsolationMode.NONE
        workerConfiguration.params(srcFile, (project.logging.level ?: project.gradle.startParameter.logLevel) <= LogLevel.DEBUG)
      }
    })
  }
}
