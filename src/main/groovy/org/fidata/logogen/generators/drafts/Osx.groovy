// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import groovy.transform.CompileStatic
import org.fidata.logogen.LogoGeneratorDescriptor

/*
   OS X App Icon

   File format: PNG
   Directory/file layout:
   Sizes in dp: 16, 32, 128, 256, 512
   Default density: 72
   Density factors: 1, 2

   References:
   1. Designing App Icons
      https://developer.apple.com/library/mac/documentation/UserExperience/Conceptual/OSXHIGuidelines/Designing.html
   2. Optimizing for High Resolution
      https://developer.apple.com/library/mac/documentation/GraphicsAnimation/Conceptual/HighResolutionOSX/Optimizing/Optimizing.html#//apple_ref/doc/uid/TP40012302-CH7-SW3
   3. https://forum.xojo.com/13390-create-icns-file/p4#p106375
   
   Notes:
   This task doesn't create .icns file.
   According to [3], it is recommended and intended to generate .icns
   during compilation for specific target version.
   Use `iconutil` under MAC.
   Xcode is also able to create .icns file - see [2].
*/
@CompileStatic
LogoGenerator('OSX') { srcFile, includeDir, outputDir, debug ->
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('osx', Osx, 'osx', null)

  outputDir = file("$outputDir/icon.iconset")
  def commands = []

  def sizes = [16, 32, 128, 256, 512]
  def defDensity = 72
  def densityFactors = [1, 2]
  for (densityFactor in densityFactors) {
    def outputFiles = []

    def args = [imconv,
    ] + (debug ? ['-verbose'] : []) + [
      '-background', 'none',
      '-density', densityFactor * defDensity,
      '-units', Units.PIXELSPERINCH.toString(),
      srcFile,
    ]
    sizes.eachWithIndex { size, i ->
      def outputFile = file("$outputDir/icon_${size}x${size}" + (densityFactor > 1 ? "@${densityFactor}x" : '') + '.png')
      def last = (i == (sizes.size() - 1))
      size *= densityFactor
      args += (last ? [] : ['(',
        '-clone', '0']) +
        ['-resize', "${size}x${size}"] +
        (last ? [] : ['-write']) + [outputFile] +
        (last ? [] : ['+delete', ')'])
      outputFiles.push outputFile
    }
    commands.push([type: Exec, commandLine: args, outputFiles: outputFiles])
  }

  return commands
}
