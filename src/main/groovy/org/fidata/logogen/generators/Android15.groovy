package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import org.fidata.logogen.LogoGeneratorDescriptor

@CompileStatic
class Android15 extends LogoGenerator {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('android1.5', Android15)

  LogoGenerator('Android-1.5') { srcFile, includeDir, outputDir, debug ->
    def size = 48
    def density = 160

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

    return [[type: Exec, commandLine: args, outputFiles: [outputFile]]]
  }
}
