package org.fidata.logogen

import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

import javax.inject.Inject
import java.awt.Color

class LogoGenExtension {
  final RegularFileProperty srcFile
  final Property<Color> background

  void setBackground(String background) {
    this.@background.set Color.decode(background)
  }

  @Inject
  LogoGenExtension(ObjectFactory objectFactory, ProjectLayout projectLayout) {
    background = objectFactory.property(Color)
    srcFile = projectLayout.fileProperty()
  }
}
