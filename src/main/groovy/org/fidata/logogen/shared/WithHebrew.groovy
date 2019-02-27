package org.fidata.logogen.shared

import groovy.transform.CompileStatic
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional

@CompileStatic
interface WithHebrew {
  /**
   * Source file for Hebrew locale, in SVG format.
   * Provided as separate since Hebrew has different rules
   * on mirroring characters (specifically, question mark is not mirrored).
   * If not provided then TODO
   */
  @InputFile
  @Optional
  RegularFileProperty getHebrewSrcFile()

  /**
   * Method of creation of Hebrew icon.
   * By default it is {@link HebrewLogoGenerationMethod#SEPARATE_SOURCE}
   * if {@link #getHebrewSrcFile} is set
   * and {@link HebrewLogoGenerationMethod#STANDARD_RTL} otherwise
   */
  @Input
  Property<HebrewLogoGenerationMethod> getHebrewLogoGenerationMethod()
}
