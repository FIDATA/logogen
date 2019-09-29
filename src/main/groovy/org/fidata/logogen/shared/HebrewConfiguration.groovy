package org.fidata.logogen.shared

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import groovy.transform.TupleConstructor

@TupleConstructor(includeSuperProperties = true)
@Immutable(knownImmutableClasses = [File])
@CompileStatic
class HebrewConfiguration extends RtlConfiguration {
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
  HebrewLogoGenerationMethod hebrewLogoGenerationMethod
}
