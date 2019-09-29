package org.fidata.logogen.shared

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import java.awt.Color

@Immutable
@CompileStatic
class BackgroundConfiguration {
  /**
   * Background color.
   * It is used by generators that require opaque background
   */
  final Color background
}
