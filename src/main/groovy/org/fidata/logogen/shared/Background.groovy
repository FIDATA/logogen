package org.fidata.logogen.shared

import groovy.transform.CompileStatic
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import java.awt.Color

@CompileStatic
interface Background {
  /**
   * Background color.
   * It is used by generators that require opaque background
   */
  @Input
  Property<Color> getBackground()
}