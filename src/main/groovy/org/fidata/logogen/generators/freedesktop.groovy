#!/usr/bin/env groovy
/*
   freedesktop.org Application Icon Generator
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

LogoGenerator('freedesktop') { srcFile, includeDir, outputDir, debug ->
    outputDir = "$outputDir/hicolor"
	def commands = []
	def outputFiles = []
	
	def sizes = [16, 22, 24, 32, 36, 48, 64, 72, 96, 128, 192, 256, 512]

	def args = [imconv,
		] + (debug ? ['-verbose'] : []) + [
		'-background', 'none',
		'-density', '96',
		'-units', 'pixelsperinch',
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
}
