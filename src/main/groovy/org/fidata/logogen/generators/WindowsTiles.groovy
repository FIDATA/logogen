#!/usr/bin/env groovy
/*
   Windows Tiles Generator
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

import groovy.text.*

LogoGenerator('WindowsTiles') { srcFile, includeDir, outputDir, debug ->
	def commands = []

	def sizesDP = [70, 150]
	def defDensity = 96
	def densityFactors = [
		0.8,
		1.0,
		1.4,
		1.5
	]

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
	
	def outputFile = file("$outputDir/${project.group}.VisualElementsManifest.xml")
	commands.push([type: Task, doLast: {
		TemplateEngine engine = new groovy.text.XmlTemplateEngine()
		def template = engine.createTemplate(file("$includeDir/VisualElementsManifest.xml.gsp"))
		Properties binding = new Properties()
		file(srcFile + '.properties').withInputStream {
			binding.load(it)
		}
		binding['Square70x70Logo'] = 'Assets/70x70Logo.png'
		binding['Square150x150Logo'] = 'Assets/150x150Logo.png'
		outputFile.write template.make(binding).toString()
	}, outputFiles: [outputFile]])
	
	return commands
}
