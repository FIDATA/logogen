package org.fidata.logogen.shared.configurations.impl

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.fidata.logogen.shared.configurations.Default

@Immutable(knownImmutableClasses = [File])
@CompileStatic
final class DefaultImpl implements Default {
  /**
   * Source file, in SVG format
   */
  final File srcFile
}
