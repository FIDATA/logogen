package org.fidata.logogen.shared.configurations

import groovy.transform.CompileStatic
import org.fidata.logogen.shared.enums.HebrewLogoGenerationMethod

@CompileStatic
interface Hebrew extends Rtl {
  /**
   * Source file for Hebrew locale, in SVG format.
   * Provided as separate since Hebrew has different rules
   * on mirroring characters (specifically, question mark is not mirrored).
   * If not provided then TODO
   */
  File getHebrewSrcFile()

  /**
   * Method of creation of Hebrew icon.
   * By default it is {@link HebrewLogoGenerationMethod#SEPARATE_SOURCE}
   * if {@link #getHebrewSrcFile} is set
   * and {@link HebrewLogoGenerationMethod#STANDARD_RTL} otherwise
   */
  HebrewLogoGenerationMethod getHebrewLogoGenerationMethod()
}
