package org.fidata.logogen.shared.configurations.impl

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import java.awt.Color
import org.fidata.logogen.shared.configurations.Background

@Immutable
@CompileStatic
final class BackgroundImpl implements Background {
  /**
   * {@inheritDoc}
   */
  final Color background
}
