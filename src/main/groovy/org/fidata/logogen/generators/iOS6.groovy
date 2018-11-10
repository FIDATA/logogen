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

LogoGenerator('iOS6') { srcFile, includeDir, outputDir, debug ->
	def size = 120
	Properties binding = new Properties()
	file(srcFile + '.properties').withInputStream {
		binding.load(it)
	}
	
	def outputFile = file("$outputDir/${project.group}.png")
	def args = [imconv,
		] + (debug ? ['-verbose'] : []) + [
		'-background', "\"${binding['BackgroundColor']}\"",
		'-units', 'pixelsperinch',
		srcFile,
		'-resize', "${size}x${size}",
		outputFile
	]

	return [[type: Exec, commandLine: args, outputFiles: [outputFile]]]
}
