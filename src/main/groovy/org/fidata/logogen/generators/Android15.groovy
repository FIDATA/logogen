package org.fidata.logogen.generators

import com.google.common.collect.ImmutableMap
import groovy.transform.CompileStatic
import org.fidata.logogen.LogoGeneratorDescriptor
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import org.im4java.core.IMOperation

import javax.inject.Inject
import java.math.MathContext

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
final class Android15 extends LogoGenerator {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('android1.5', Android15)

  private final WorkerExecutor workerExecutor

  final static class ImageMagickConvertOperation extends LogoGenerator.ImageMagickConvertOperation {
    private static final BigDecimal SIZE_DP = 48
    private static final BigDecimal DEF_DENSITY = 160

    public static final String RES_DIR_NAME = 'res'

    private final File outputDir

    ImageMagickConvertOperation(File srcFile, boolean debug, File outputDir) {
      super(srcFile, debug)
      this.@outputDir = outputDir
    }

    @Override
    protected IMOperation getOperation() {
      File resOutputDir = new File(outputDir, RES_DIR_NAME)

      IMOperation operation = new IMOperation()
      operation.background('none')

      def outputFile = file("$outputDir/res/drawable/ic_launcher.png")
      def args = [
        imconv,
      ] + (debug ? ['-verbose'] : []) + [
        '-background', 'none',
        '-density', density,
        '-units', 'pixelsperinch',
        srcFile,
        '-resize', "${size}x${size}",
        outputFile
      ]


      DENSITY_FACTORS.each { String densityName, BigDecimal densityValue ->
        File densityOutputDir = new File(resOutputDir, "mipmap-${densityName}")
        assert densityOutputDir.mkdirs()
        String outputFile = new File(densityOutputDir, 'ic_launcher.png').toString()

        int size = (densityValue * SIZE_DP).round(MathContext.UNLIMITED).intValueExact()
        operation.openOperation()
          .clone(0)
          .units('pixelsperinch')
          .density((densityValue * DEF_DENSITY).round(MathContext.UNLIMITED).intValueExact())
          .resize(size, size)
          .write(outputFile)
        operation.closeOperation()
      }
      operation.delete('0--1')
      operation
    }
  }

  @Inject
  Android15(WorkerExecutor workerExecutor) {
    this.@workerExecutor = workerExecutor
  }

  @TaskAction
  protected void resizeAndConvert() {
    imageMagicConvert workerExecutor, ImageMagickConvertOperation, outputDir.get().asFile
  }
}
