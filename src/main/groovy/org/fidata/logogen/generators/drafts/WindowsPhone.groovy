// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import groovy.transform.CompileStatic
import org.fidata.logogen.LogoGeneratorDescriptor

/*
   LaunchPad Team Images

   File format: ?
   Sizes: 14 (Icon), 64 (Logo), 192 (Mugshot)

   References:
   
*/
@CompileStatic
LogoGenerator('WindowsPhone') { srcFile, includeDir, outputDir, debug ->
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('windowsPhone', WindowsPhone, 'windowsPhone', null)

  def outputFiles = []

  def sizes = [
    'Icon': 14,
    'Logo': 64,
    'Mugshot': 192
  ]

  def args = [imconv,
  ] + (debug ? ['-verbose'] : []) + [
    '-background', 'none',
    '-units', Units.PIXELSPERINCH.toString(),
    srcFile,
  ]
  sizes.eachWithIndex { size, i ->
    def outputFile = file("$outputDir/${size.key}.png")
    def last = (i == (sizes.size() - 1))
    args += (last ? [] : ['(',
      '-clone', '0']) +
      ['-resize', "${size.value}x${size.value}"] +
      (last ? [] : ['-write']) + [outputFile] +
      (last ? [] : ['+delete', ')'])
    outputFiles.push outputFile
  }

  return [[type: Exec, commandLine: args, outputFiles: outputFiles]]
}
