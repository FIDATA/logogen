// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.shared

import groovy.transform.CompileStatic
import javax.annotation.Nullable
import org.gradle.api.Transformer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

@CompileStatic
final class NameConfigurationProperty implements WithName, Property<NameConfiguration> {
  final Property<String> logoName

  NameConfigurationProperty(ObjectFactory objectFactory, ProviderFactory providerFactory) {
    this.@logoName = objectFactory.property(String)
    this.@provider = providerFactory.provider {
      new NameConfiguration(logoName.get())
    }
  }

  @Delegate
  private final Provider<NameConfiguration> provider

  private Transformer<String, NameConfiguration> logoNameTransformer = { NameConfiguration nameConfiguration -> // TOTEST
    nameConfiguration.logoName
  }

  @Override
  void set(@Nullable NameConfiguration nameConfiguration) {
    value nameConfiguration
  }

  @Override
  void set(Provider<? extends NameConfiguration> provider) {
    this.@logoName.set provider.map(logoNameTransformer)
  }

  @Override
  Property<NameConfiguration> value(@Nullable NameConfiguration nameConfiguration) {
    this.@logoName.set nameConfiguration?.logoName
    this
  }

  @Override
  Property<NameConfiguration> convention(NameConfiguration nameConfiguration) {
    this.@logoName.convention nameConfiguration.logoName
    this
  }

  @Override
  Property<NameConfiguration> convention(Provider<? extends NameConfiguration> provider) {
    this.@logoName.convention provider.map(logoNameTransformer)
    this
  }

  @Override
  void finalizeValue() {
    this.@logoName.finalizeValue()
  }
}
