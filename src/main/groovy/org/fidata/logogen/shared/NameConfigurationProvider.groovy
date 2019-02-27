package org.fidata.logogen.shared

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

class NameConfigurationProvider implements WithName {
  final Property<String> logoName

  NameConfigurationProvider(ObjectFactory objectFactory) {
    this.@logoName = objectFactory.property(String)
  }
}
