#!/usr/bin/env groovy
/*
 * Windows Tiles Generator
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
import org.gradle.api.Action
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.TaskAction
import com.google.common.collect.ImmutableList
import org.gradle.workers.IsolationMode
import org.gradle.workers.WorkerConfiguration
import org.gradle.workers.WorkerExecutor
import org.im4java.core.IMOperation
import javax.inject.Inject
import java.math.MathContext

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
@CompileStatic
final class WindowsTiles extends LogoGenerator {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('windowsTiles', WindowsTiles)

  static final class ImageMagicConvertRunnable extends LogoGenerator.ImageMagickConvertRunnable {
    private static final List<BigDecimal> SIZES_DP = ImmutableList.of(
      70.0,
      150.0
    )
    private static final BigDecimal DEF_DENSITY = 96
    private static final List<BigDecimal> DENSITY_FACTORS = ImmutableList.of(
      0.8,
      1.0,
      1.4,
      1.5,
    )

    @Override
    protected void configureOperation(IMOperation operation) {
      operation.background('none')
      DENSITY_FACTORS.each { BigDecimal densityValue ->
        String outputFile = "$outputDir/res/mipmap-${densityName}/ic_launcher.png" // TODO
        int size = (densityValue * SIZE_DP).round(MathContext.UNLIMITED).intValueExact()
        operation.openOperation()
        operation.units('pixelsperinch')
        operation.density((densityValue * DEF_DENSITY).round(MathContext.UNLIMITED).intValueExact())
        operation.resize(size, size)
        operation.addImage(outputFile)
        operation.closeOperation()
      }


        '-density', densityFactor * defDensity,
        '-units', 'pixelsperinch',
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


    }
  }

  static final class GenerateTemplateRunnable extends LogoGenerator.GenerateTemplateRunnable {
    @Inject
    GenerateTemplateRunnable() {
      super('VisualElementsManifest.xml.gsp')
    }

    @Override
    protected Map getBindings() {
      [
        'Square70x70Logo': 'Assets/70x70Logo.png',
        'Square150x150Logo': 'Assets/150x150Logo.png',
      ]
    }
  }

  @Inject
  WindowsTiles(WorkerExecutor workerExecutor) {
    this.@workerExecutor = workerExecutor
  }

  @TaskAction
  protected final void generate() {
    workerExecutor.submit(ImageMagicConvertRunnable, new Action<WorkerConfiguration>() {
      @Override
      void execute(WorkerConfiguration workerConfiguration) {
        workerConfiguration.isolationMode = IsolationMode.NONE
        workerConfiguration.params(srcFile, (project.logging.level ?: project.gradle.startParameter.logLevel) <= LogLevel.DEBUG)
      }
    })
    workerExecutor.submit(GenerateTemplateRunnable, new Action<WorkerConfiguration>() {
      @Override
      void execute(WorkerConfiguration workerConfiguration) {
        workerConfiguration.isolationMode = IsolationMode.NONE
        workerConfiguration.params(outputDir.file("${project.group}.VisualElementsManifest.xml"))
      }
    })
  }
}


LogoGenerator('WindowsTiles') { srcFile, includeDir, outputDir, debug ->
  def commands = []

  for (densityFactor in densityFactors) {
    def outputFiles = []
    def args = [imconv,
    ] + (debug ? ['-verbose'] : []) + [
      '-background', 'none',
      '-density', densityFactor * defDensity,
      '-units', 'pixelsperinch',
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
}
