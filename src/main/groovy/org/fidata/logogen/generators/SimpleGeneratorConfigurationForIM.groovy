package org.fidata.logogen.generators

import org.fidata.logogen.shared.configurations.Default
import org.fidata.logogen.shared.configurations.Name

interface SimpleGeneratorConfigurationForIM extends SimpleGeneratorConfiguration {
  int getSize()
  String getFormat()
  Integer getDensity()
}
