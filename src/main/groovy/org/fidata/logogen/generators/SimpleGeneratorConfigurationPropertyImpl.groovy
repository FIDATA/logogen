// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import javax.annotation.Nullable
import javax.inject.Inject
import org.fidata.logogen.shared.properties.ConfigurableDefault
import org.fidata.logogen.shared.properties.ConfigurableName
import org.fidata.logogen.shared.properties.impl.DefaultPropertyImpl
import org.fidata.logogen.shared.properties.impl.NamePropertyImpl
import org.fidata.utils.ImmutableWithCustomConstructors
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

@ImmutableWithCustomConstructors
@CompileStatic
final class SimpleGeneratorConfigurationPropertyImpl implements ConfigurableSimpleGeneratorConfiguration, Provider<SimpleGeneratorConfigurationForIM>, Property<SimpleGeneratorConfiguration> {
  @Delegate(includeTypes = [ConfigurableDefault]/* TOTEST , methodAnnotations = true */)
  private final DefaultPropertyImpl configurableDefault
  @Delegate(includeTypes = [ConfigurableName]/* TOTEST , methodAnnotations = true */)
  private final NamePropertyImpl configurableName

  @Delegate
  private final Provider<? extends SimpleGeneratorConfigurationForIM> provider

  @Inject
  SimpleGeneratorConfigurationPropertyImpl(ProviderFactory providerFactory, ObjectFactory objectFactory, ProjectLayout projectLayout, int size, String format, Integer density) {
    this.@configurableDefault = new DefaultPropertyImpl(providerFactory, objectFactory, projectLayout)
    this.@configurableName = new NamePropertyImpl(providerFactory, objectFactory)
    this.@provider = providerFactory.provider {
      new SimpleGeneratorConfigurationForIMImpl(
        configurableDefault.srcFile.get().asFile,
        configurableName.logoName.get(),
        size,
        format,
        density
      )
    }
  }

  // WORKAROUND: @Delegate doesn't support both includes and includeTypes
  ProviderFactory getProviderFactory() {
    this.@configurableDefault.providerFactory
  }

  @Override
  void set(@Nullable SimpleGeneratorConfiguration simpleGeneratorConfiguration) {
    value simpleGeneratorConfiguration
  }

  @Override
  void set(Provider<? extends SimpleGeneratorConfiguration> provider) {
    this.@configurableDefault.set provider
    this.@configurableName.set provider
  }

  @Override
  Property<SimpleGeneratorConfiguration> value(@Nullable SimpleGeneratorConfiguration simpleGeneratorConfiguration) {
    this.@configurableDefault.value simpleGeneratorConfiguration
    this.@configurableName.value simpleGeneratorConfiguration
    this
  }

  @Override
  Property<SimpleGeneratorConfiguration> convention(SimpleGeneratorConfiguration simpleGeneratorConfiguration) {
    this.@configurableDefault.convention simpleGeneratorConfiguration
    this.@configurableName.convention simpleGeneratorConfiguration
    this
  }

  @Override
  Property<SimpleGeneratorConfiguration> convention(Provider<? extends SimpleGeneratorConfiguration> provider) {
    this.@configurableDefault.convention provider
    this.@configurableName.convention provider
    this
  }

  @Override
  void finalizeValue() {
    this.@configurableDefault.finalizeValue()
    this.@configurableName.finalizeValue()
  }
}