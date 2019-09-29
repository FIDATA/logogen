package org.fidata.logogen.shared

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import groovy.transform.TupleConstructor

@TupleConstructor(includeSuperProperties = true)
@Immutable(knownImmutableClasses = [File])
@CompileStatic
class RtlConfiguration extends DefaultConfiguration {
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
