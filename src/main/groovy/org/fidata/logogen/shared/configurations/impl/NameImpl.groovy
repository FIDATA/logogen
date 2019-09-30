package org.fidata.logogen.shared.configurations.impl

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.fidata.logogen.shared.configurations.Name

@Immutable
@CompileStatic
final class NameImpl implements Name {
  /**
   * {@inheritDoc}
   */
  final String logoName
}
