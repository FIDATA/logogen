// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import javax.annotation.Nullable
import javax.inject.Inject
import org.fidata.logogen.shared.properties.ConfigurableDefault
import org.fidata.logogen.shared.properties.impl.DefaultPropertyImpl
import org.fidata.utils.ImmutableWithCustomConstructors
import org.gradle.api.Transformer
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.provider.SetProperty

@ImmutableWithCustomConstructors
@CompileStatic
final class FreeDesktopConfigurationPropertyImpl implements ConfigurableFreeDesktopConfiguration, Property<FreeDesktopConfiguration> {
  @Delegate(includeTypes = [ConfigurableDefault]/* TOTEST , methodAnnotations = true */)
  private final DefaultPropertyImpl configurableDefault
  final Property<String> theme
  final SetProperty<Integer> sizes

  @Delegate
  private final Provider<? extends FreeDesktopConfiguration> provider

  @Inject
  FreeDesktopConfigurationPropertyImpl(ProviderFactory providerFactory, ObjectFactory objectFactory, ProjectLayout projectLayout) {
    this.@configurableDefault = new DefaultPropertyImpl(providerFactory, objectFactory, projectLayout)
    this.@theme = objectFactory.property(String)
    this.@sizes = objectFactory.setProperty(Integer)
    this.@provider = providerFactory.provider {
      new FreeDesktopConfigurationImpl(
        configurableDefault.srcFile.get().asFile,
        theme.get(),
        (Set<Integer>)sizes.get(),
      )
    }
  }

  // WORKAROUND: @Delegate doesn't support both includes and includeTypes
  ProviderFactory getProviderFactory() {
    this.@configurableDefault.providerFactory
  }

  private static final Transformer<String, FreeDesktopConfiguration> THEME_TRANSFORMER = new Transformer<String, FreeDesktopConfiguration>() {
    @Override
    String transform(FreeDesktopConfiguration freeDesktopConfiguration) {
      freeDesktopConfiguration.theme
    }
  }

  private static final Transformer<Set<Integer>, FreeDesktopConfiguration> SIZES_TRANSFORMER = new Transformer<Set<Integer>, FreeDesktopConfiguration>() {
    @Override
    Set<Integer> transform(FreeDesktopConfiguration freeDesktopConfiguration) {
      freeDesktopConfiguration.sizes
    }
  }

  @Override
  void set(@Nullable FreeDesktopConfiguration freeDesktopConfiguration) {
    value freeDesktopConfiguration
  }

  @Override
  void set(Provider<? extends FreeDesktopConfiguration> provider) {
    this.@configurableDefault.set provider
    this.@theme.set provider.map(THEME_TRANSFORMER)
    this.@sizes.set provider.map(SIZES_TRANSFORMER)
  }

  @Override
  Property<FreeDesktopConfiguration> value(@Nullable FreeDesktopConfiguration freeDesktopConfiguration) {
    this.@configurableDefault.value freeDesktopConfiguration
    this.@theme.set freeDesktopConfiguration?.theme
    this.@sizes.set freeDesktopConfiguration?.sizes
    this
  }

  @Override
  Property<FreeDesktopConfiguration> convention(FreeDesktopConfiguration freeDesktopConfiguration) {
    this.@configurableDefault.convention freeDesktopConfiguration
    this.@theme.convention providerFactory.provider { freeDesktopConfiguration.theme }
    this.@sizes.convention providerFactory.provider { freeDesktopConfiguration.sizes }
    this
  }

  @Override
  Property<FreeDesktopConfiguration> convention(Provider<? extends FreeDesktopConfiguration> provider) {
    this.@configurableDefault.convention provider
    this.@theme.convention provider.map(THEME_TRANSFORMER)
    this.@sizes.convention provider.map(SIZES_TRANSFORMER)
    this
  }

  @Override
  void finalizeValue() {
    this.@configurableDefault.finalizeValue()
    this.@theme.finalizeValue()
    this.@sizes.finalizeValue()
  }
}