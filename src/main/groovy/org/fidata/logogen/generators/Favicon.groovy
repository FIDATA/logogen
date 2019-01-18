#!/usr/bin/env groovy
/*
 * Website icons and tiles Generator
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
// How to Make a Favicon
// http://blog.teamtreehouse.com/how-to-make-a-favicon
// Pinned Sites: Windows 7 Desktop Integration with Internet Explorer 9
// https://msdn.microsoft.com/ru-RU/library/gg131029.aspx
// https://msdn.microsoft.com/library/gg491740(v=vs.85).aspx
LogoGenerator('Favicon') { srcFile, includeDir, outputDir, debug ->
  def sizes = [16, 24, 32, 48, 64]

  def outputFile = file("$outputDir/favicon.ico")
  def args = [imconv,
  ] + (debug ? ['-verbose'] : []) + [
    '-background', 'none',
    '-units', 'pixelsperinch',
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
