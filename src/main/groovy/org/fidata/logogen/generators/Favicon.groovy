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
/*
   Windows Tiles for Pinned Website

   File format: PNG [6]
   Directory/file layout:
   Sizes:
    scaled by 1.8: 70x70, 150x150 [5]
      310x150, 310x310
    not scaled: 144x144 [6]
   Default density: 96

   References:
   1. Pinned Sites
      https://msdn.microsoft.com/library/hh772707%28v=vs.85%29.aspx
   2. Pinned site enhancements
      https://msdn.microsoft.com/en-us/library/bg183312%28v=vs.85%29.aspx
   3. Browser configuration schema reference
      https://msdn.microsoft.com/en-us/library/dn320426%28v=vs.85%29.aspx
   4. Pinned site metadata reference
      https://msdn.microsoft.com/en-us/library/dn255024(v=vs.85).aspx
   5. Creating custom tiles for IE11 websites
      https://msdn.microsoft.com/en-US/library/dn455106%28v=vs.85%29.aspx
   6. Высококачественные визуальные элементы для закрепленных сайтов в Windows 8
      http://blogs.msdn.com/b/ie_ru/archive/2012/06/08/high-quality-visuals-for-pinned-sites-in-windows-8.aspx
*/

import groovy.text.*

LogoGenerator('WindowsTilesForPinnedWebsite') { srcFile, includeDir, outputDir, debug ->
  def commands = []
  def outputFiles = []

  def sizes = [70, 150]
  def defDensity = 96
  def densityFactor = 1.8

  def args = [imconv,
  ] + (debug ? ['-verbose'] : []) + [
    '-background', 'none',
    '-density', densityFactor * defDensity,
    '-units', 'pixelsperinch',
    srcFile,
  ]
  sizes.each { size ->
    def outputFile = file("$outputDir/${size}x${size}Logo.png")
    size *= densityFactor
    args += ['(',
      '-clone', '0',
      '-resize', "${size}x${size}",
      '-write', outputFile,
      '+delete',
      ')']
    outputFiles.push outputFile
  }

  def size = 144

  def outputFile = file("$outputDir/${size}x${size}Tile.png") // TODO
  args += [
    '-resize', "${size}x${size}",
    outputFile,
  ]
  outputFiles.push outputFile
  commands.push([type: Exec, commandLine: args, outputFiles: outputFiles])

  outputFile = file("$outputDir/browserconfig.xml")
  commands.push([type: Task, doLast: {
    TemplateEngine engine = new groovy.text.XmlTemplateEngine()
    def template = engine.createTemplate(file("$includeDir/browserconfig.xml.gsp"))
    Properties binding = new Properties()
    file(srcFile + '.properties').withInputStream {
      binding.load(it)
    }
    binding['Square70x70Logo'] = '70x70Logo.png'
    binding['Square150x150Logo'] = '150x150Logo.png'
    binding['Wide310x150Logo'] = '310x150Logo.png'
    binding['Square310x310Logo'] = '310x310Logo.png'
    outputFile.write template.make(binding).toString()
  }, outputFiles: [outputFile]])

  outputFile = file("$outputDir/WindowsTilesForWebsite.html")
  commands.push([type: Task, doLast: {
    XmlTemplateEngine engine = new groovy.text.XmlTemplateEngine()
    def template = engine.createTemplate(file("$includeDir/favicon.html.gsp"))
    Map binding = [
      'website_name': project.group,
      'msapplication_config': 'msapplication-config.xml',
      'TileImage': '', // TODO
    ]
    outputFile.write template.make(binding).toString()
  }, outputFiles: [outputFile]])

  return commands
}


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
