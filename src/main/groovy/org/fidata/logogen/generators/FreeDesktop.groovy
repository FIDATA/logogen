#!/usr/bin/env groovy
/*
 * freedesktop.org Application Icon Generator
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
import org.fidata.imagemagick.Units
import org.fidata.logogen.LogoGeneratorDescriptor
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import org.im4java.core.IMOperation

import javax.inject.Inject

/*
   Free Desktop

   File format: PNG (required)/XPM (supported for backward
     compatibility)
   Directory/file layout:
     * {theme}/{size}x{size}/apps/{appname}.png
     * {theme}/{size}x{size}/apps/{appname}.icon - in INI format
       (optional, isn't generated here)
   Default theme: hicolor (required)
   Sizes: 16, 22, 24, 32, 36, 48, 64, 72, 96, 128, 192, 256, 512
   Default density: 96

   File format: SVG
   Directory/file layout:
     * {theme}/scalable/apps/{appname}.png
     * {theme}/scalable/apps/{appname}.icon - in INI format
       (optional, isn't generated here)
   Default theme: hicolor (required)

   References:
   1. Icon Theme Specification
      http://standards.freedesktop.org/icon-theme-spec/icon-theme-spec-latest.html
   2. xdg/default-icon-theme
      http://cgit.freedesktop.org/xdg/default-icon-theme/tree/index.theme
   3. Themed Icons
      https://developer.gnome.org/ThemedIcons/
   4. Tango Icon Theme Guidelines
      http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines
*/
@CompileStatic
final class FreeDesktop extends LogoGenerator /* TODO: WithRtl? */ {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('freeDesktop', FreeDesktop, null)

  outputDir = "$outputDir/hicolor"
  def commands = []
  def outputFiles = []

  def sizes = [16, 22, 24, 32, 36, 48, 64, 72, 96, 128, 192, 256, 512]

  def args = [imconv,
  ] + (debug ? ['-verbose'] : []) + [
    '-background', 'none',
    '-density', '96',
    '-units', Units.PIXELSPERINCH.toString(),
    srcFile,
  ]
  sizes.eachWithIndex { size, i ->
    def outputFile = file("$outputDir/${size}x${size}/apps/${project.group}.png")
    def last = (i == (sizes.size() - 1))
    args += (last ? [] : ['(',
      '-clone', '0']) +
      ['-resize', "${size}x${size}"] +
      (last ? [] : ['-write']) + [outputFile] +
      (last ? [] : ['+delete', ')'])
    outputFiles.push outputFile
  }
  commands.push([type: Exec, commandLine: args, outputFiles: outputFiles])

  def outputFile = file("$outputDir/scalable/${project.group}.svg")
  args = [
    'svgo',
    srcFile,
    '--config=' + file("$includePath/freedesktop.svgo.config.json"),
    outputFile
  ]
  commands.push([type: CrossPlatformExec, commandLine: args, outputFiles: [outputFile]])

  return commands

  protected final static class ImageMagickConvertOperation extends LogoGenerator.ImageMagickConvertOperation {
    public static final String MAINICON_ICO_FILE_NAME = 'MAINICON.ico'

    private final WindowsMainIconConfiguration configuration

    ImageMagickConvertOperation(File srcFile, boolean debug, File outputDir, WindowsMainIconConfiguration configuration) {
      super(srcFile, debug, outputDir)
      this.@configuration = configuration
    }

    @Override
    protected IMOperation getOperation() {
      IMOperation operation = new IMOperation()
      operation
        .background('none')
        .units(Units.PIXELSPERINCH.toString())

      File outputFile = new File(super.outputDir, MAINICON_ICO_FILE_NAME)

      configuration.depths.each { Integer depth, WindowsMainIconConfiguration.ColorDepth colorDepth ->
        IMOperation colorReductionOperation = colorDepth.colorReduction?.toIMOperation()
        for (Integer size in colorDepth.sizes) {
          operation.openOperation()
            .clone(0)
          if (colorReductionOperation != null) {
            operation.addOperation(colorReductionOperation)
          }
          if (depth != null) { // TODO
            operation.colors(2**depth)
          }
          operation.resize(size, size)
          if (depth != null) { // TODO
            operation.depth(depth)
          }
          operation.closeOperation()
        }
      }

      operation
        .delete(0)
        .compress(configuration.compress.toString())
        .write(outputFile.toString())

      operation
    }
  }

  private final WorkerExecutor workerExecutor

  @Inject
  FreeDesktop(WorkerExecutor workerExecutor) {
    this.@workerExecutor = workerExecutor
  }

  @TaskAction
  protected void resizeAndConvert() {
    imageMagicConvert workerExecutor, ImageMagickConvertOperation
  }
}
