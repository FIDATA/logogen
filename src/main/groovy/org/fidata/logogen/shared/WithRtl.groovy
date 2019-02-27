package org.fidata.logogen.shared

import groovy.transform.CompileStatic
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional

@CompileStatic
interface WithRtl {
  /**
   * Source file for RTL locales, in SVG format
   */
  @InputFile
  @Optional
  RegularFileProperty getRtlSrcFile()

  /**
   * Method of creation of RTL icon.
   * By default it is {@link RtlLogoGenerationMethod#SEPARATE_SOURCE}
   * if {@link #getRtlSrcFile} is set
   * and {@link RtlLogoGenerationMethod#MIRROW} otherwise
   */
  @Input
  Property<RtlLogoGenerationMethod> getRtlLogoGenerationMethod()
}
