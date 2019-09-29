// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.shared.properties

import groovy.transform.CompileStatic
import org.fidata.logogen.shared.enums.RtlLogoGenerationMethod
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional

@CompileStatic
interface ConfigurableRtl {
  /**
   * Source file for RTL locales, in SVG format
   */
  @InputFile
  @Optional
  RegularFileProperty getRtlSrcFile()

  /**
   * Method of creation of RTL icon.
   * By default it is {@link org.fidata.logogen.shared.enums.RtlLogoGenerationMethod#SEPARATE_SOURCE}
   * if {@link #getRtlSrcFile} is set
   * and {@link org.fidata.logogen.shared.enums.RtlLogoGenerationMethod#MIRROW} otherwise
   */
  @Input
  Property<RtlLogoGenerationMethod> getRtlLogoGenerationMethod()
}
