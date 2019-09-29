// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.shared.properties

import groovy.transform.CompileStatic
import org.fidata.logogen.shared.enums.HebrewLogoGenerationMethod
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional

@CompileStatic
interface ConfigurableHebrew {
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
   * By default it is {@link org.fidata.logogen.shared.enums.HebrewLogoGenerationMethod#SEPARATE_SOURCE}
   * if {@link #getHebrewSrcFile} is set
   * and {@link org.fidata.logogen.shared.enums.HebrewLogoGenerationMethod#STANDARD_RTL} otherwise
   */
  @Input
  Property<HebrewLogoGenerationMethod> getHebrewLogoGenerationMethod()
}
