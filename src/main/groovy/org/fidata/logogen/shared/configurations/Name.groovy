package org.fidata.logogen.shared.configurations

import groovy.transform.CompileStatic

@CompileStatic
interface Name extends Serializable {
  String getLogoName()
}
