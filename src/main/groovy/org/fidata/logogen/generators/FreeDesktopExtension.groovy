// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import javax.inject.Inject
import org.fidata.groovy.utils.ImmutableWithCustomConstructors
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory

@ImmutableWithCustomConstructors
@CompileStatic
final class FreeDesktopExtension {
  @Delegate
  private final ConfigurableFreeDesktopConfiguration freeDesktopConfigurationProvider

  @Inject
  FreeDesktopExtension(ProviderFactory providerFactory, ObjectFactory objectFactory, ProjectLayout projectLayout) {
    this.@freeDesktopConfigurationProvider = new FreeDesktopConfigurationPropertyImpl(providerFactory, objectFactory, projectLayout)
    theme.convention providerFactory.provider { 'hicolor' }
    sizes.convention providerFactory.provider {
      [16, 22, 24, 32, 36, 48, 64, 72, 96, 128, 192, 256, 512]
    }
  }
}
