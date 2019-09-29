package org.fidata.logogen.shared.configurations

import groovy.transform.CompileStatic
import org.fidata.logogen.shared.enums.RtlLogoGenerationMethod

@CompileStatic
interface Rtl extends Serializable {
  /**
   * Source file for RTL locales, in SVG format
   */
  File getRtlSrcFile()

  /**
   * Method of creation of RTL icon.
   * By default it is {@link RtlLogoGenerationMethod#SEPARATE_SOURCE}
   * if {@link #getRtlSrcFile} is set
   * and {@link RtlLogoGenerationMethod#MIRROW} otherwise
   */
  RtlLogoGenerationMethod getRtlLogoGenerationMethod()
}
