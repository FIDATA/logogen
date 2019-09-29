// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.shared.providers

import groovy.transform.CompileStatic
import org.fidata.logogen.shared.configurations.Default
import org.fidata.logogen.shared.properties.ConfigurableDefault
import org.gradle.api.provider.Provider

@CompileStatic
interface DefaultProvider extends ConfigurableDefault, Provider<Default> {
}