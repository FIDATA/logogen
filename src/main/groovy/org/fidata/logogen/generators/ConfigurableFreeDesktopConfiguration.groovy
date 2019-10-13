// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import org.fidata.logogen.shared.properties.ConfigurableDefault
import org.fidata.logogen.shared.properties.ConfigurableName
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input

@CompileStatic
interface ConfigurableFreeDesktopConfiguration extends ConfigurableDefault, ConfigurableName {
  @Input
  Property<String> getTheme()

  @Input
  SetProperty<Integer> getSizes()
}
