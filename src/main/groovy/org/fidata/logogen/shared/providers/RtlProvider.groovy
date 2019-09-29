// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.shared.providers

import groovy.transform.CompileStatic
import org.fidata.logogen.shared.configurations.Rtl
import org.fidata.logogen.shared.properties.ConfigurableRtl
import org.gradle.api.provider.Provider

@CompileStatic
interface RtlProvider extends ConfigurableRtl, Provider<Rtl> {
}