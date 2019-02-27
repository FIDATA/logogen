#!/usr/bin/env groovy
/*
 * OS X App Icon Generator
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
   OS X App Icon

   File format: PNG
   Directory/file layout:
   Sizes in dp: 16, 32, 128, 256, 512
   Default density: 72
   Density factors: 1.0, 2.0

   References:
   1. Designing App Icons
      https://developer.apple.com/library/mac/documentation/UserExperience/Conceptual/OSXHIGuidelines/Designing.html
   2. Optimizing for High Resolution
      https://developer.apple.com/library/mac/documentation/GraphicsAnimation/Conceptual/HighResolutionOSX/Optimizing/Optimizing.html#//apple_ref/doc/uid/TP40012302-CH7-SW3
   3. https://forum.xojo.com/13390-create-icns-file/p4#p106375
   
   Notes:
   This task doesn't create .icns file. Use `iconutil` under MAC.
   Accorging to [3], it is recommended and intended to generate .icns
   during compilation for specific target version. Xcode is also able
   to create .icns file - see [2].
*/
@CompileStatic
LogoGenerator('iOS6') { srcFile, includeDir, outputDir, debug ->
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('ios6', Ios6, Classifiers.IOS, null)

  def size = 120
  Properties binding = new Properties()
  file(srcFile + '.properties').withInputStream {
    binding.load(it)
  }

  def outputFile = file("$outputDir/${project.group}.png")
  def args = [imconv,
  ] + (debug ? ['-verbose'] : []) + [
    '-background', "\"${binding['BackgroundColor']}\"",
    '-units', Units.PIXELSPERINCH.toString(),
    srcFile,
    '-resize', "${size}x${size}",
    outputFile
  ]

  return [[type: Exec, commandLine: args, outputFiles: [outputFile]]]
}
