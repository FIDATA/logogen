package org.fidata.logogen.shared

import groovy.transform.CompileStatic
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

@CompileStatic
class HebrewLogoConfigurationProvider implements HebrewLogo {
  final RegularFileProperty hebrewSrcFile

  final Property<HebrewLogoGenerationMethod> hebrewLogoGenerationMethod

  HebrewLogoConfigurationProvider(ObjectFactory objectFactory) {
    this.@hebrewSrcFile = objectFactory.fileProperty()
    this.@hebrewLogoGenerationMethod = objectFactory.property(HebrewLogoGenerationMethod)
  }
}
