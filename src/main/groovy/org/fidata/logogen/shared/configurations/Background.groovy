package org.fidata.logogen.shared.configurations

import groovy.transform.CompileStatic
import java.awt.Color

@CompileStatic
interface Background {
  /**
   * Background color.
   * It is used by generators that require opaque background
   */
  Color getBackground()
}
