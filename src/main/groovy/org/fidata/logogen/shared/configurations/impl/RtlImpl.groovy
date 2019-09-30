package org.fidata.logogen.shared.configurations.impl

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.fidata.logogen.shared.configurations.Rtl
import org.fidata.logogen.shared.enums.RtlLogoGenerationMethod
import org.fidata.utils.ImmutableWithCustomConstructors

@ImmutableWithCustomConstructors(knownImmutableClasses = [File])
@CompileStatic
final class RtlImpl implements Rtl {
  @Delegate
  private final DefaultImpl aDefault

  /**
   * {@inheritDoc}
   */
  final File rtlSrcFile

  /**
   * {@inheritDoc}
   */
  final RtlLogoGenerationMethod rtlLogoGenerationMethod

  RtlImpl(File srcFile, File rtlSrcFile, RtlLogoGenerationMethod rtlLogoGenerationMethod) {
    this.@aDefault = new DefaultImpl(srcFile)
    this.@rtlSrcFile = rtlSrcFile
    this.@rtlLogoGenerationMethod = rtlLogoGenerationMethod
  }
}
