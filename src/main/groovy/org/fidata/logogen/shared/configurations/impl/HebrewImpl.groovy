package org.fidata.logogen.shared.configurations.impl

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.fidata.logogen.shared.configurations.Hebrew
import org.fidata.logogen.shared.enums.HebrewLogoGenerationMethod
import org.fidata.logogen.shared.enums.RtlLogoGenerationMethod
import org.fidata.utils.ImmutableWithCustomConstructors

@ImmutableWithCustomConstructors(knownImmutableClasses = [File])
@CompileStatic
final class HebrewImpl implements Hebrew {
  @Delegate
  private final RtlImpl rtl

  /**
   * {@inheritDoc}
   */
  final File hebrewSrcFile

  /**
   * {@inheritDoc}
   */
  final HebrewLogoGenerationMethod hebrewLogoGenerationMethod

  HebrewImpl(File srcFile, File rtlSrcFile, RtlLogoGenerationMethod rtlLogoGenerationMethod, File hebrewSrcFile, HebrewLogoGenerationMethod hebrewLogoGenerationMethod) {
    this.@rtl = new RtlImpl(srcFile, rtlSrcFile, rtlLogoGenerationMethod)
    this.@hebrewSrcFile = hebrewSrcFile
    this.@hebrewLogoGenerationMethod = hebrewLogoGenerationMethod
  }
}
