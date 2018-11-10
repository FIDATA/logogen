#!/usr/bin/env groovy
/*
   Android Launcher Icon Generator
   Copyright (C) 2015  Basil Peace

   This file is part of Logo Generator.

   Redistribution and use, with or without modification, are permitted
   provided that the following condition is met:

    1. Redistributions of this code must retain the above copyright
       notice, this condition and the following disclaimer.

   THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR
   IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
   WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
   ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
   CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
   EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
   PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
   OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
   (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
   OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
   DAMAGE. */

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
