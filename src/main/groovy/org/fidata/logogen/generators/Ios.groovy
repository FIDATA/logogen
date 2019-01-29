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
   File format: PNG
   Directory/file layout:
   Density-independent sizes in px: 1024
   Sizes in dp: 16, 32, 128, 256, 512
   Default density: 72
   Density factors: 1, 2, 3
*/
@CompileStatic
LogoGenerator('iOS') { srcFile, includeDir, outputDir, debug ->
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('ios', Ios, null)

  // outputDir = file("$outputDir/icon.iconset")
  def commands = []
  def outputFiles = []

  def sizes = [
    'iTunesArtwork': 512,
    'Icon-60': 60,
    'Icon-76': 76,
    'Icon-Small-40': 40,
    'Icon-Small': 29,
  ]
  def defDensity = 72
  Properties binding = new Properties()
  file(srcFile + '.properties').withInputStream {
    binding.load(it)
  }

  def args = [imconv,
  ] + (debug ? ['-verbose'] : []) + [
    '-background', "\"${binding['BackgroundColor']}\"",
    '-density', defDensity,
    '-units', Units.PIXELSPERINCH.toString(),
    srcFile,
  ]
  sizes.eachWithIndex { size, i ->
    def outputFile = file("$outputDir/${size.key}")
    def last = (i == (sizes.size() - 1))
    args += (last ? [] : ['(',
      '-clone', '0']) +
      ['-resize', "${size.value}x${size.value}",
        '-format', 'PNG'] +
      (last ? [] : ['-write']) + [outputFile] +
      (last ? [] : ['+delete', ')'])
    outputFiles.push outputFile
  }
  commands.push([type: Exec, commandLine: args, outputFiles: outputFiles])

  outputFiles = []

  args = [imconv,
  ] + (debug ? ['-verbose'] : []) + [
    '-background', 'none',
    '-density', 2 * defDensity,
    '-units', Units.PIXELSPERINCH.toString(),
    srcFile,
  ]
  sizes.eachWithIndex { size, i ->
    size.value *= 2
    def outputFile = file("$outputDir/${size.key}@2x")
    def last = (i == (sizes.size() - 1))
    args += (last ? [] : ['(',
      '-clone', '0']) +
      ['-resize', "${size.value}x${size.value}",
        '-format', 'PNG'] +
      (last ? [] : ['-write']) + [outputFile] +
      (last ? [] : ['+delete', ')'])
    outputFiles.push outputFile
  }
  commands.push([type: Exec, commandLine: args, outputFiles: outputFiles])

/*task ios(type: Exec) {
  for (size in sizes) {
    commandLine imconv,
      // '-verbose',
      '-background', 'none',
      '-density', str(defResolutionValue),
      '-units', Units.PIXELSPERINCH.toString(),
      srcFilename,
      '-resize', "${size}x${size}",
      "$outputDir/CFBundleIconFile_" + String.format('%dx%d.png', size, size)
    size *= 2
    commandLine imconv,
      // '-verbose',
      '-background', 'none',
      '-density', str(defResolutionValue * 2),
      '-units', Units.PIXELSPERINCH.toString(),
      srcFilename,
      '-resize', "${size}x${size}",
      "$outputDir/CFBundleIconFile_" + String.format('%dx%d@2x.png', size, size),
  }
  // This task doesn't create .icns file
  // Use `iconutil` under MAC
  // Accorging to https://forum.xojo.com/13390-create-icns-file/53,
  // it is recommended and intended to generate .icns during compilation
  // for specific target version
  // Alternatives for other platforms:
  // *  libicns (currently supports only 128?128 and smaller images, and compatibility is under the question)
  // *  Wine's windowscodecs
}
*/
  return commands
}