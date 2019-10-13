// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import org.fidata.logogen.shared.configurations.Default
import org.fidata.logogen.shared.configurations.Name
import org.fidata.logogen.shared.configurations.impl.DefaultImpl
import org.fidata.logogen.shared.configurations.impl.NameImpl
import org.fidata.groovy.utils.ImmutableWithCustomConstructors

@ImmutableWithCustomConstructors
@CompileStatic
final class SimpleGeneratorConfigurationImpl implements SimpleGeneratorConfiguration {
  @Delegate
  private final Default aDefault
  @Delegate
  private final Name name

  SimpleGeneratorConfigurationImpl(File srcFile, String logoName) {
    this.@aDefault = new DefaultImpl(srcFile)
    this.@name = new NameImpl(logoName)
  }
}
