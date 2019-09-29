// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import groovy.transform.CompileStatic
import org.fidata.logogen.LogoGeneratorDescriptor

/**
 * How to Make a Favicon
 * http://blog.teamtreehouse.com/how-to-make-a-favicon
 * Pinned Sites: Windows 7 Desktop Integration with Internet Explorer 9
 * https://msdn.microsoft.com/ru-RU/library/gg131029.aspx
 * https://msdn.microsoft.com/library/gg491740(v=vs.85).aspx
 */
@CompileStatic
LogoGenerator('Favicon') { srcFile, includeDir, outputDir, debug ->
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('favicon', Favicon, Classifiers.WEBSITE, null)

  def sizes = [16, 24, 32, 48, 64]

  def outputFile = file("$outputDir/favicon.ico")
  def args = [imconv,
  ] + (debug ? ['-verbose'] : []) + [
    '-background', 'none',
    '-units', Units.PIXELSPERINCH.toString(),
    srcFile
  ]
  for (size in sizes)
    args += ['(',
      '-clone', '0',
      '-resize', "${size}x${size}",
      '-compress', (size < 256 ? 'Zip' : 'None'),
      ')']
  args += [
    '-delete', '0',
    outputFile
  ]

  return [[type: Exec, commandLine: args, outputFiles: [outputFile]]]
}
