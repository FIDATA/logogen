package org.fidata.logogen.shared.configurations.impl

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import java.awt.Color
import org.fidata.logogen.shared.configurations.Background

@Immutable
@CompileStatic
final class BackgroundImpl implements Background {
  /**
   * Background color.
   * It is used by generators that require opaque background
   */
  final Color background
}
