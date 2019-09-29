// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.shared.providers

import groovy.transform.CompileStatic
import org.fidata.logogen.shared.configurations.Hebrew
import org.fidata.logogen.shared.properties.ConfigurableHebrew
import org.gradle.api.provider.Provider

@CompileStatic
interface HebrewProvider extends ConfigurableHebrew, Provider<Hebrew> {
}