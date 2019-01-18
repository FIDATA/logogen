package org.fidata.logogen.generators

LogoGenerator('webclips') { srcFile, includeDir, outputDir, debug ->
  def outputFiles = []

  def sizes = [60, 76, 120, 152]

  def args = [imconv,
  ] + (debug ? ['-verbose'] : []) + [
    '-background', 'none',
    '-density', '96',
    '-units', 'pixelsperinch',
    srcFile,
  ]
  sizes.eachWithIndex { size, i ->
    def outputFile = file("$outputDir/apple-touch-icon-${size}x${size}-precomposed.png")
    def last = (i == (sizes.size() - 1))
    args += (last ? [] : ['(',
      '-clone', '0']) +
      ['-resize', "${size}x${size}"] +
      (last ? [] : ['-write']) + [outputFile] +
      (last ? [] : ['+delete', ')'])
    outputFiles.push outputFile
  }

  return [[type: Exec, commandLine: args, outputFiles: outputFiles]]
}
