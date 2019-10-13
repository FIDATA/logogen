// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import org.fidata.groovy.utils.ImmutableWithCustomConstructors

@ImmutableWithCustomConstructors
@CompileStatic
final class SimpleGeneratorConfigurationForIMImpl implements SimpleGeneratorConfigurationForIM {
  @Delegate
  private final SimpleGeneratorConfiguration simpleGeneratorConfiguration
  final int size
  final String format
  final Integer density

  SimpleGeneratorConfigurationForIMImpl(File srcFile, String logoName, int size, String format, Integer density = null) {
    this.@simpleGeneratorConfiguration = new SimpleGeneratorConfigurationImpl(srcFile, logoName)
    this.@size = size
    this.@format = format
    this.@density = density
  }
}
