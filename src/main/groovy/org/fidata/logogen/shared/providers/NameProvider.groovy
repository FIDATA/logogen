// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.shared.providers

import groovy.transform.CompileStatic
import org.fidata.logogen.shared.configurations.Name
import org.fidata.logogen.shared.properties.ConfigurableName
import org.gradle.api.provider.Provider

@CompileStatic
interface NameProvider extends ConfigurableName, Provider<Name> {
}