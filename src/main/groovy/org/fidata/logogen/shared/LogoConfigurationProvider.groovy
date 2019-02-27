package org.fidata.logogen.shared

import groovy.transform.CompileStatic
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory

@CompileStatic
class LogoConfigurationProvider implements Logo {
  final RegularFileProperty srcFile

  LogoConfigurationProvider(ObjectFactory objectFactory) {
    this.@srcFile = objectFactory.fileProperty()
  }
}
