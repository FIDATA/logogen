package org.fidata.logogen.generators

class Android15 {
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
