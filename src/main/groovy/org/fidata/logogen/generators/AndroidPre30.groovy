package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import org.fidata.logogen.LogoGeneratorDescriptor

@CompileStatic
class AndroidPre30 extends LogoGenerator {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('androidPre3.0', AndroidPre30)

  LogoGenerator('Android-pre3.0') { srcFile, includeDir, outputDir, debug ->
    def commands = []

    def sizeDP = 48
    def defDensity = 160
    def densityFactors = [
      'ldpi':    0.75,
      'mdpi':    1.0,
      'tvdpi':   1.33,
      'hdpi':    1.5,
      'xhdpi':   2.0,
      'xxhdpi':  3.0,
      'xxxhdpi': 4.0,
    ]

    for (densityFactor in densityFactors) {
      def outputFile = file("$outputDir/res/drawable-${densityFactor.key}/ic_launcher.png")
      def size = Math.round(densityFactor.value * sizeDP)
      def args = [
        imconv,
      ] + (debug ? ['-verbose'] : []) + [
        '-background', 'none',
        '-density', densityFactor.value * defDensity,
        '-units', 'pixelsperinch',
        srcFile,
        '-resize', "${size}x${size}",
        outputFile
      ]
      commands.push([type: Exec, commandLine: args, outputFiles: [outputFile]])
    }

    return commands
  }

}
