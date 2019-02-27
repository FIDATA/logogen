package org.fidata.logogen.shared

import groovy.transform.CompileStatic
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import java.awt.Color

@CompileStatic
class BackgroundConfigurationProvider implements Background {
  final Property<Color> background

  /**
   * Sets background color by name.
   * In current version it is decoded with {@link Color#decode(String)}.
   * This could be changed in the future to support more color names
   *
   * @param background Color name
   */
  void setBackground(String background) {
    this.@background.set Color.decode(background)
  }

  BackgroundConfigurationProvider(ObjectFactory objectFactory) {
    this.@background = objectFactory.property(Color)
  }
}
