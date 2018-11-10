#!/usr/bin/env groovy
/*
   OS X App Icon Generator
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
   OS X App Icon

   File format: PNG
   Directory/file layout:
   Sizes in dp: 16, 32, 128, 256, 512
   Default density: 72
   Density factors: 1, 2

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

LogoGenerator('OSX') { srcFile, includeDir, outputDir, debug ->
	outputDir = file("$outputDir/icon.iconset")
	def commands = []

	def sizes = [16, 32, 128, 256, 512]
	def defDensity = 72
	def densityFactors = [1, 2]
	for (densityFactor in densityFactors) {
		def outputFiles = []

		def args = [imconv,
			] + (debug ? ['-verbose'] : []) + [
			'-background', 'none',
			'-density', densityFactor * defDensity,
			'-units', 'pixelsperinch',
			srcFile,
		]
		sizes.eachWithIndex { size, i ->
			def outputFile = file("$outputDir/icon_${size}x${size}" + (densityFactor > 1 ? "@${densityFactor}x" : '') + '.png')
			def last = (i == (sizes.size() - 1))
			size *= densityFactor
			args += (last ? [] : ['(',
				'-clone', '0']) +
				['-resize', "${size}x${size}"] +
				(last ? [] : ['-write']) + [outputFile] +
				(last ? [] : ['+delete', ')'])
			outputFiles.push outputFile
		}
		commands.push([type: Exec, commandLine: args, outputFiles: outputFiles])
	}

	return commands
}
