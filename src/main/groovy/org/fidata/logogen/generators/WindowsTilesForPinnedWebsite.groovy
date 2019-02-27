/*
   Windows Tiles for Pinned Website

   File format: PNG [6]
   Directory/file layout:
   Sizes:
    scaled by 1.8: 70x70, 150x150 [5]
      310x150, 310x310
    not scaled: 144x144 [6]
   Default density: 96

   References:
   1. Pinned Sites
      https://msdn.microsoft.com/library/hh772707%28v=vs.85%29.aspx
   2. Pinned site enhancements
      https://msdn.microsoft.com/en-us/library/bg183312%28v=vs.85%29.aspx
   3. Browser configuration schema reference
      https://msdn.microsoft.com/en-us/library/dn320426%28v=vs.85%29.aspx
   4. Pinned site metadata reference
      https://msdn.microsoft.com/en-us/library/dn255024(v=vs.85).aspx
   5. Creating custom tiles for IE11 websites
      https://msdn.microsoft.com/en-US/library/dn455106%28v=vs.85%29.aspx
   6. Высококачественные визуальные элементы для закрепленных сайтов в Windows 8
      http://blogs.msdn.com/b/ie_ru/archive/2012/06/08/high-quality-visuals-for-pinned-sites-in-windows-8.aspx
*/
package org.fidata.logogen.generators

import groovy.text.*
import groovy.transform.CompileStatic
import org.fidata.logogen.LogoGeneratorDescriptor

@CompileStatic
LogoGenerator('WindowsTilesForPinnedWebsite') { srcFile, includeDir, outputDir, debug ->
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('windowsTilesForPinnedWebsite', WindowsTilesForPinnedWebsite, Classifiers.WEBSITE, null)

  def commands = []
  def outputFiles = []

  def sizes = [70, 150]
  def defDensity = 96
  def densityFactor = 1.8

  def args = [imconv,
  ] + (debug ? ['-verbose'] : []) + [
    '-background', 'none',
    '-density', densityFactor * defDensity,
    '-units', Units.PIXELSPERINCH.toString(),
    srcFile,
  ]
  sizes.each { size ->
    def outputFile = file("$outputDir/${size}x${size}Logo.png")
    size *= densityFactor
    args += ['(',
      '-clone', '0',
      '-resize', "${size}x${size}",
      '-write', outputFile,
      '+delete',
      ')']
    outputFiles.push outputFile
  }

  def size = 144

  def outputFile = file("$outputDir/${size}x${size}Tile.png") // TODO
  args += [
    '-resize', "${size}x${size}",
    outputFile,
  ]
  outputFiles.push outputFile
  commands.push([type: Exec, commandLine: args, outputFiles: outputFiles])

  outputFile = file("$outputDir/browserconfig.xml")
  commands.push([type: Task, doLast: {
    TemplateEngine engine = new groovy.text.XmlTemplateEngine()
    def template = engine.createTemplate(file("$includeDir/browserconfig.xml.gsp"))
    Properties binding = new Properties()
    file(srcFile + '.properties').withInputStream {
      binding.load(it)
    }
    binding['Square70x70Logo'] = '70x70Logo.png'
    binding['Square150x150Logo'] = '150x150Logo.png'
    binding['Wide310x150Logo'] = '310x150Logo.png'
    binding['Square310x310Logo'] = '310x310Logo.png'
    outputFile.write template.make(binding).toString()
  }, outputFiles: [outputFile]])

  outputFile = file("$outputDir/WindowsTilesForWebsite.html")
  commands.push([type: Task, doLast: {
    XmlTemplateEngine engine = new groovy.text.XmlTemplateEngine()
    def template = engine.createTemplate(file("$includeDir/windowsTilesForPinnedWebsite.html.gsp"))
    Map binding = [
      'website_name': project.group,
      'msapplication_config': 'msapplication-config.xml',
      'TileImage': '', // TODO
    ]
    outputFile.write template.make(binding).toString()
  }, outputFiles: [outputFile]])

  return commands
}
