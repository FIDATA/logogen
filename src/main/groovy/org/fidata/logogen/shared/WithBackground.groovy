// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.shared

import groovy.transform.CompileStatic
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import java.awt.Color

@CompileStatic
interface WithBackground extends Provider<BackgroundConfiguration> {
  /**
   * Background color.
   * It is used by generators that require opaque background
   */
  @Input
  Property<Color> getBackground()
}