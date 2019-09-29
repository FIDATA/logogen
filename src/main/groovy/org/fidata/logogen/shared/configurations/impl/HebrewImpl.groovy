package org.fidata.logogen.shared.configurations.impl

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.fidata.logogen.shared.configurations.Hebrew
import org.fidata.logogen.shared.enums.HebrewLogoGenerationMethod

@Immutable(knownImmutableClasses = [File])
@CompileStatic
final class HebrewImpl implements Hebrew {
  /**
   * Source file for Hebrew locale, in SVG format.
   * Provided as separate since Hebrew has different rules
   * on mirroring characters (specifically, question mark is not mirrored).
   * If not provided then TODO
   */
  final File hebrewSrcFile

  /**
   * Method of creation of Hebrew icon.
   * By default it is {@link HebrewLogoGenerationMethod#SEPARATE_SOURCE}
   * if {@link #hebrewSrcFile} is set
   * and {@link HebrewLogoGenerationMethod#STANDARD_RTL} otherwise
   */
  final HebrewLogoGenerationMethod hebrewLogoGenerationMethod
}
