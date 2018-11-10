#!/usr/bin/env groovy
/*
   GitHub Organization Profile Picture Generator
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
   GitHub Organization Profile Picture

   File format: PNG/JPG/GIF
   Size: 500

   References:
   1. How do I set up my profile picture?
      https://help.github.com/articles/how-do-i-set-up-my-profile-picture/
*/

LogoGenerator('GitHub') { srcFile, includeDir, outputDir, debug ->
	def size = 500

	def outputFile = file("$outputDir/${project.group}.png")
	def args = [imconv,
		] + (debug ? ['-verbose'] : []) + [
		'-background', 'none',
		'-units', 'pixelsperinch',
		srcFile,
		'-resize', "${size}x${size}",
		outputFile
	]

	return [[type: Exec, commandLine: args, outputFiles: [outputFile]]]
}
