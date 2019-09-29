// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.shared

import groovy.transform.CompileStatic
import javax.annotation.Nullable
import org.gradle.api.Transformer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import java.awt.Color
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

@CompileStatic
final class BackgroundConfigurationProperty implements WithBackground, Property<BackgroundConfiguration> {
  final ProviderFactory providerFactory
  final Property<Color> background

  /**
   * Sets background color by name.
   * In current version it is decoded with {@link Color#decode(String)}.
   * This could be changed in the future to support more color names
   *
   * @param background Color name
   */
  void setBackground(String background) {
    this.@background.set Color.decode(background)
  }

  BackgroundConfigurationProperty(ObjectFactory objectFactory, ProviderFactory providerFactory) {
    this.@background = objectFactory.property(Color)
    this.@providerFactory = providerFactory
    this.@provider = providerFactory.provider {
      new BackgroundConfiguration(background.get())
    }
  }

  @Delegate
  private final Provider<BackgroundConfiguration> provider

  private Transformer<Color, BackgroundConfiguration> colorTransformer = { BackgroundConfiguration backgroundConfiguration -> // TOTEST
    backgroundConfiguration.background
  }

  @Override
  void set(@Nullable BackgroundConfiguration backgroundConfiguration) {
    value backgroundConfiguration
  }

  @Override
  void set(Provider<? extends BackgroundConfiguration> provider) {
    this.@background.set provider.map(colorTransformer)
  }

  @Override
  Property<BackgroundConfiguration> value(@Nullable BackgroundConfiguration backgroundConfiguration) {
    this.@background.set backgroundConfiguration?.background
    this
  }

  @Override
  Property<BackgroundConfiguration> convention(BackgroundConfiguration backgroundConfiguration) {
    this.@background.convention backgroundConfiguration.background
    this
  }

  @Override
  Property<BackgroundConfiguration> convention(Provider<? extends BackgroundConfiguration> provider) {
    this.@background.convention provider.map(colorTransformer)
    this
  }

  @Override
  void finalizeValue() {
    this.@background.finalizeValue()
  }
}
