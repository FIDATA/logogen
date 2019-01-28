#!/usr/bin/env groovy
/*
 * LaunchPad Team Images Generator
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
package org.fidata.logogen.generators

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
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('windowsPhone', WindowsPhone)

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
