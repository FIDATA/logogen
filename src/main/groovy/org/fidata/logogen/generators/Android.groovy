#!/usr/bin/env groovy
/*
 * Android Launcher Icon Generator
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
   Android Launcher Icon

   File format: PNG/JPG/GIF
   Directory/file layout:
     Android 3.0+: res/mipmap-{density}/ic_launcher.png
     Android before 3.0: res/drawable-{density}/ic_launcher.png [7]
     Android 1.5: res/drawable/ic_launcher.png [8]
   Sizes in dp: 48
   Default density: 160
   Density factors: 0.75 (lpi), 1.0 (mdpi, default), 1.33 (tvdpi),
     1.5 (hdpi), 2.0 (xhdpi), 3.0 (xxhdpi), 4.0 (xxxhpdi)
     Android 1.5 doesn't support density factors (i.e. mdpi only) [8]

   References:
   1. Use Assets Designed for Tablet Screens // Tablet App Quality
      http://developer.android.com/distribute/essentials/quality/tablets.html#use-tablet-icons
   2. Supporting Different Densities
      http://developer.android.com/training/multiscreen/screendensities.html
   3. Managing Launcher Icons as mipmap Resources
      http://developer.android.com/tools/projects/index.html#mipmap
   4. Icons
      https://www.google.com/design/spec/style/icons.html
   5. http://www.androiddesign.tips/
   6. http://android-developers.blogspot.ru/2013/07/making-beautiful-android-app-icons.html
   7. http://developer.android.com/reference/android/R.mipmap.html
   8. http://tekeye.biz/2013/android-icon-size
*/

LogoGenerator('Android') { srcFile, includeDir, outputDir, debug ->
  def commands = []

  def sizeDP = 48
  def defDensity = 160
  def densityFactors = [
    'ldpi':    0.75,
    'mdpi':    1.0,
    'tvdpi':   1.33,
    'hdpi':    1.5,
    'xhdpi':   2.0,
    'xxhdpi':  3.0,
    'xxxhdpi': 4.0,
  ]

  for (densityFactor in densityFactors) {
    def outputFile = file("$outputDir/res/mipmap-${densityFactor.key}/ic_launcher.png")
    def size = Math.round(densityFactor.value * sizeDP)
    def args = [
      imconv,
    ] + (debug ? ['-verbose'] : []) + [
      '-background', 'none',
      '-density', densityFactor.value * defDensity,
      '-units', 'pixelsperinch',
      srcFile,
      '-resize', "${size}x${size}",
      outputFile
    ]
    commands.push([type: Exec, commandLine: args, outputFiles: [outputFile]])
  }

  return commands
}

LogoGenerator('Android-pre3.0') { srcFile, includeDir, outputDir, debug ->
  def commands = []

  def sizeDP = 48
  def defDensity = 160
  def densityFactors = [
    'ldpi':    0.75,
    'mdpi':    1.0,
    'tvdpi':   1.33,
    'hdpi':    1.5,
    'xhdpi':   2.0,
    'xxhdpi':  3.0,
    'xxxhdpi': 4.0,
  ]

  for (densityFactor in densityFactors) {
    def outputFile = file("$outputDir/res/drawable-${densityFactor.key}/ic_launcher.png")
    def size = Math.round(densityFactor.value * sizeDP)
    def args = [
      imconv,
    ] + (debug ? ['-verbose'] : []) + [
      '-background', 'none',
      '-density', densityFactor.value * defDensity,
      '-units', 'pixelsperinch',
      srcFile,
      '-resize', "${size}x${size}",
      outputFile
    ]
    commands.push([type: Exec, commandLine: args, outputFiles: [outputFile]])
  }

  return commands
}

LogoGenerator('Android-1.5') { srcFile, includeDir, outputDir, debug ->
  def size = 48
  def density = 160

  def outputFile = file("$outputDir/res/drawable/ic_launcher.png")
  def args = [
    imconv,
  ] + (debug ? ['-verbose'] : []) + [
    '-background', 'none',
    '-density', density,
    '-units', 'pixelsperinch',
    srcFile,
    '-resize', "${size}x${size}",
    outputFile
  ]

  return [[type: Exec, commandLine: args, outputFiles: [outputFile]]]
}
