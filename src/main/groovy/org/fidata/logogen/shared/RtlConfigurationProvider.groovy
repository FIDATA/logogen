package org.fidata.logogen.shared

import groovy.transform.CompileStatic
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

@CompileStatic
class RtlConfigurationProvider implements WithRtl {
  final RegularFileProperty rtlSrcFile

  final Property<RtlLogoGenerationMethod> rtlLogoGenerationMethod

  RtlConfigurationProvider(ObjectFactory objectFactory) {
    this.@rtlSrcFile = objectFactory.fileProperty()
    this.@rtlLogoGenerationMethod = objectFactory.property(RtlLogoGenerationMethod)
  }
}
