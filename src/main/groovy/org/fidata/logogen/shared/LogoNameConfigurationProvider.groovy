package org.fidata.logogen.shared

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

class LogoNameConfigurationProvider implements LogoName {
  final Property<String> logoName

  LogoNameConfigurationProvider(ObjectFactory objectFactory) {
    this.@logoName = objectFactory.property(String)
  }
}
