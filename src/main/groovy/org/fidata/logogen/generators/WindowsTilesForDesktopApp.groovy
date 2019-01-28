#!/usr/bin/env groovy
/*
 * Windows Tiles for Desktop App Generator
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
/*
   Windows Tiles for Desktop App

   File format: PNG/JPG/GIF
   Directory/file layout: resource naming convention with qualifiers for
     scale etc. See [1], [2]
   Sizes in dp: 70, 150
   Default density: 96
   Density factors: 0.8, 1.0, 1.4, 1.8

   References:
   1. How to customize Start screen tiles for desktop apps (Windows Runtime apps)
      https://msdn.microsoft.com/en-us/library/windows/apps/dn393983.aspx
   2. How to name resources using qualifiers (HTML)
      https://msdn.microsoft.com/en-us/library/windows/apps/hh965372.aspx
*/
package org.fidata.logogen.generators

import groovy.text.*
import groovy.transform.CompileStatic
import org.fidata.logogen.LogoGeneratorDescriptor

@CompileStatic
LogoGenerator('WindowsTilesForDesktopApp') { srcFile, includeDir, outputDir, debug ->
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('windowsTilesForDesktopApp', WindowsTilesForDesktopApp)

  def commands = []

  def sizesDP = [70, 150]
  def defDensity = 96
  def densityFactors = [
    0.8,
    1.0,
    1.4,
    1.5
  ]

  for (densityFactor in densityFactors) {
    def outputFiles = []
    def args = [imconv,
    ] + (debug ? ['-verbose'] : []) + [
      '-background', 'none',
      '-density', densityFactor * defDensity,
      '-units', Units.PIXELSPERINCH.toString(),
      srcFile,
    ]
    sizesDP.eachWithIndex { sizeDP, i ->
      def outputFile = file("$outputDir/Assets/${sizeDP}x${sizeDP}Logo.scale-${densityFactor * 100}.png")
      def size = Math.round(densityFactor * sizeDP)
      def last = (i == (sizesDP.size() - 1))
      args += (last ? [] : ['(',
        '-clone', '0']) +
        ['-resize', "${size}x${size}"] +
        (last ? [] : ['-write']) + [outputFile] +
        (last ? [] : ['+delete', ')'])
      outputFiles.push outputFile
    }
    commands.push([type: Exec, commandLine: args, outputFiles: outputFiles])
  }

  def outputFile = file("$outputDir/${project.group}.VisualElementsManifest.xml")
  commands.push([type: Task, doLast: {
    TemplateEngine engine = new groovy.text.XmlTemplateEngine()
    def template = engine.createTemplate(file("$includeDir/VisualElementsManifest.xml.gsp"))
    Properties binding = new Properties()
    file(srcFile + '.properties').withInputStream {
      binding.load(it)
    }
    binding['Square70x70Logo'] = 'Assets/70x70Logo.png'
    binding['Square150x150Logo'] = 'Assets/150x150Logo.png'
    outputFile.write template.make(binding).toString()
  }, outputFiles: [outputFile]])

  return commands
}
