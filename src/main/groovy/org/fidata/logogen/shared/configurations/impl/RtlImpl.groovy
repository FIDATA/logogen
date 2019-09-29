package org.fidata.logogen.shared.configurations.impl

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.fidata.logogen.shared.configurations.Rtl
import org.fidata.logogen.shared.enums.RtlLogoGenerationMethod

@Immutable(knownImmutableClasses = [File])
@CompileStatic
final class RtlImpl implements Rtl {
  /**
   * Source file for RTL locales, in SVG format
   */
  final File rtlSrcFile

  /**
   * Method of creation of RTL icon.
   * By default it is {@link RtlLogoGenerationMethod#SEPARATE_SOURCE}
   * if {@link #rtlSrcFile} is set
   * and {@link RtlLogoGenerationMethod#MIRROW} otherwise
   */
  final RtlLogoGenerationMethod rtlLogoGenerationMethod
}
