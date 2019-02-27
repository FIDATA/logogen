package org.fidata.logogen.shared

import groovy.transform.CompileStatic
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile

@CompileStatic
interface Logo {
  /**
   * Source file, in SVG format
   */
  @InputFile
  RegularFileProperty getSrcFile()
}
