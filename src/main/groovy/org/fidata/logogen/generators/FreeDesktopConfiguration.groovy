package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import org.fidata.logogen.shared.configurations.Default
import org.fidata.logogen.shared.configurations.Name

@CompileStatic
interface FreeDesktopConfiguration extends Default, Name {
  String getTheme()

  Set<Integer> getSizes()
}