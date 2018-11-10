#!/usr/bin/env groovy
/*
   LaunchPad Team Images Generator
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
   LaunchPad Team Images

   File format: ?
   Sizes: 14 (Icon), 64 (Logo), 192 (Mugshot)

   References:
   1. https://launchpad.net/~{organization}/+branding
*/

LogoGenerator('LaunchPad') { srcFile, includeDir, outputDir, debug ->
	def outputFiles = []

	def sizes = [
		'Icon': 14,
		'Logo': 64,
		'Mugshot': 192
	]

	def args = [imconv,
		] + (debug ? ['-verbose'] : []) + [
		'-background', 'none',
		'-units', 'pixelsperinch',
		srcFile,
	]
	sizes.eachWithIndex { size, i ->
		def outputFile = file("$outputDir/${size.key}.png")
		def last = (i == (sizes.size() - 1))
		args += (last ? [] : ['(',
			'-clone', '0']) +
			['-resize', "${size.value}x${size.value}"] +
			(last ? [] : ['-write']) + [outputFile] +
			(last ? [] : ['+delete', ')'])
		outputFiles.push outputFile
	}

	return [[type: Exec, commandLine: args, outputFiles: outputFiles]]
}
