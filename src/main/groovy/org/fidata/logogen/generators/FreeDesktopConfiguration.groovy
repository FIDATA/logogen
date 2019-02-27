package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import groovy.transform.Immutable

@Immutable
@CompileStatic
class FreeDesktopConfiguration implements Serializable {
  final String theme

  final Set<Integer> sizes
}
