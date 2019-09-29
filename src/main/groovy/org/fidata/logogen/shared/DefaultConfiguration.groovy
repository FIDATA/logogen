package org.fidata.logogen.shared

import groovy.transform.CompileStatic
import groovy.transform.Immutable

@Immutable(knownImmutableClasses = [File])
@CompileStatic
class DefaultConfiguration implements Serializable {
  /**
   * Source file, in SVG format
   */
  final File srcFile
}
