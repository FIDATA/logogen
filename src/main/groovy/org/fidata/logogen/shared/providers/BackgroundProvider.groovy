// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.shared.providers

import groovy.transform.CompileStatic
import org.fidata.logogen.shared.configurations.Background
import org.fidata.logogen.shared.properties.ConfigurableBackground
import org.gradle.api.provider.Provider

@CompileStatic
interface BackgroundProvider extends ConfigurableBackground, Provider<Background> {
}