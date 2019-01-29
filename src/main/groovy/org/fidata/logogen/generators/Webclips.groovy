package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import org.fidata.logogen.LogoGeneratorDescriptor

@CompileStatic
LogoGenerator('webclips') { srcFile, includeDir, outputDir, debug ->
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('webclips', Webclips, null)

  def outputFiles = []

  def sizes = [60, 76, 120, 152]

  def args = [imconv,
  ] + (debug ? ['-verbose'] : []) + [
    '-background', 'none',
    '-density', '96',
    '-units', Units.PIXELSPERINCH.toString(),
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
