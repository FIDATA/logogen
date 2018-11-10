#!/usr/bin/env groovy
/*
   Windows Application Icon (MAINICON) Generator
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
   Windows Application Icon (MAINICON)

   File format: ICO
     Windows XP doesn't support compression [5]
   Directory/file layout: all icons in one multipage MAINICON resource
     of executable file. Recommended sort order: by color depth ASC,
     then by size DESC
   Density-independent sizes in px: 768 (Windows 10 Preview),
     512 (Windows 10 Preview), 256 (Windows Vista+)
   Sizes in dp: 16, 32, 48
   Default density: 96
   Density factors: 1.0 to 5.0
     Most common: 1.0, 1.25, 1.5, 2.0
   Complete set of sizes: 768, 512, 256, 96, 72, 64, 60, 48, 40, 32, 24,
     20, 16 -- TODO
   Additional sizes: 128, 22, 14, 10, 8 -- TODO
   Color depth: 4-, 8-, 32-bit

   References:
   1. Icons
      https://msdn.microsoft.com/en-us/library/dn742485.aspx
   2. Creating Windows XP Icons
      https://msdn.microsoft.com/en-us/library/ms997636.aspx
   3. http://wiki.lazarus.freepascal.org/Windows_Icon
   4. http://www.hhhh.org/cloister/csharp/icons/
   5. Setting Window.Icon to Vista 256x256 PNG icon causes a FileFormatException
      https://social.msdn.microsoft.com/Forums/vstudio/en-US/9e723d51-0e26-4609-8fe1-5e0edcf7385b/setting-windowicon-to-vista-256x256-png-icon-causes-a-fileformatexception?forum=wpf
*/

LogoGenerator('WindowsMAINICON') { srcFile, includeDir, outputDir, debug ->
	def sizes = [/*768, 512,*/ 256, 96, 72, 64, 60, 48, 40, 32, 24, 20, 16]
	
	def outputFile = file("$outputDir/MAINICON.ico")
	def args = [imconv,
		] + (debug ? ['-verbose'] : []) + [
		'-background', 'none',
		'-units', 'pixelsperinch',
		srcFile
	]
	for (size in sizes)
		args += ['(',
			'-clone', '0',
			// See http://www.imagemagick.org/Usage/quantize/#intro
			// TODO: These settings may be not the best for some logos.
			// Make a way to customize them
			'+dither', '-posterize', 3, '-colors', 16,
			'-resize', "${size}x${size}", '-depth', 4,
		')']
	for (size in sizes)
		args += ['(',
			'-clone', '0',
			'+dither', '-colors', 256,
			'-resize', "${size}x${size}", '-depth', 8,
		')']
	for (size in sizes)
		args += ['(',
			'-clone', '0',
			'-resize', "${size}x${size}",
		')']
	args += [
		'-delete', '0',
		'-compress', 'None',
		outputFile
	]
	
	return [[type: Exec, commandLine: args, outputFiles: [outputFile]]]
}
