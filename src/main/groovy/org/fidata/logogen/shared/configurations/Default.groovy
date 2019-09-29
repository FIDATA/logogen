package org.fidata.logogen.shared.configurations

import groovy.transform.CompileStatic

@CompileStatic
interface Default extends Serializable {
  /**
   * Source file, in SVG format
   */
  File getSrcFile()
}
