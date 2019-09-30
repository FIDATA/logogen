package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import org.fidata.logogen.shared.configurations.Default

@CompileStatic
interface FreeDesktopConfiguration extends Default {
  String getTheme()

  Set<Integer> getSizes()
}