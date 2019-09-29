// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.shared.properties

import groovy.transform.CompileStatic
import java.awt.Color
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

@CompileStatic
interface ConfigurableBackground {
  /**
   * Background color.
   * It is used by generators that require opaque background
   */
  @Input
  Property<Color> getBackground()
}