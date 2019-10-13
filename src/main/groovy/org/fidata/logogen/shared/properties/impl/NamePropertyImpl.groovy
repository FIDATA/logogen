// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.shared.properties.impl

import groovy.transform.CompileStatic
import javax.annotation.Nullable
import javax.inject.Inject
import org.fidata.logogen.shared.configurations.Name
import org.fidata.logogen.shared.configurations.impl.NameImpl
import org.fidata.logogen.shared.properties.ConfigurableName
import org.fidata.groovy.utils.ImmutableWithCustomConstructors
import org.gradle.api.Transformer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

@ImmutableWithCustomConstructors
@CompileStatic
final class NamePropertyImpl implements ConfigurableName, Property<Name> {
  final Property<String> logoName

  @Delegate
  private final Provider<Name> provider

  @Inject
  NamePropertyImpl(ProviderFactory providerFactory, ObjectFactory objectFactory) {
    this.@logoName = objectFactory.property(String)
    this.@provider = providerFactory.provider {
      (Name)new NameImpl(logoName.get())
    }
  }

  private static final Transformer<String, Name> LOGO_NAME_TRANSFORMER = new Transformer<String, Name>() {
    @Override
    String transform(Name nameConfiguration) {
      nameConfiguration.logoName
    }
  }

  @Override
  void set(@Nullable Name nameConfiguration) {
    value nameConfiguration
  }

  @Override
  void set(Provider<? extends Name> provider) {
    this.@logoName.set provider.map(LOGO_NAME_TRANSFORMER)
  }

  @Override
  Property<Name> value(@Nullable Name nameConfiguration) {
    this.@logoName.set nameConfiguration?.logoName
    this
  }

  @Override
  Property<Name> convention(Name nameConfiguration) {
    this.@logoName.convention nameConfiguration.logoName
    this
  }

  @Override
  Property<Name> convention(Provider<? extends Name> provider) {
    this.@logoName.convention provider.map(LOGO_NAME_TRANSFORMER)
    this
  }

  @Override
  void finalizeValue() {
    this.@logoName.finalizeValue()
  }
}
