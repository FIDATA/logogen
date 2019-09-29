// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input

class FreeDesktopConfigurationProviderImpl {
  @Input
  final Property<String> theme

  @Input
  final SetProperty<Integer> sizes

  @Delegate
  private final Provider<FreeDesktopConfiguration> provider

  FreeDesktopConfigurationProviderImpl(ProviderFactory providerFactory, ObjectFactory objectFactory) {
    this.@theme = objectFactory.property(String)
    this.@sizes = objectFactory.setProperty(Integer)

    this.@provider = providerFactory.provider {
      new FreeDesktopConfiguration(
        theme.get(),
        sizes.get(),
      )
    }
  }
}