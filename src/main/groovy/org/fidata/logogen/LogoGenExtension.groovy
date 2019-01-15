package org.fidata.logogen

import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory

import javax.inject.Inject

class LogoGenExtension {
  final RegularFileProperty srcFile
  @Inject
  LogoGenExtension(ProjectLayout projectLayout) {
    srcFile = projectLayout.fileProperty()
  }
}
