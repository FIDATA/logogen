package org.fidata.logogen.shared

import groovy.transform.CompileStatic
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory

@CompileStatic
class DefaultConfigurationProvider implements Default {
  final RegularFileProperty srcFile

  DefaultConfigurationProvider(ObjectFactory objectFactory) {
    this.@srcFile = objectFactory.fileProperty()
  }
}
