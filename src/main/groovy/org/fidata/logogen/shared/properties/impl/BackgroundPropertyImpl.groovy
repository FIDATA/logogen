// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.shared.properties.impl

import groovy.transform.CompileStatic
import java.awt.Color
import javax.annotation.Nullable
import javax.inject.Inject
import org.fidata.logogen.shared.configurations.Background
import org.fidata.logogen.shared.configurations.impl.BackgroundImpl
import org.fidata.logogen.shared.providers.BackgroundProvider
import org.gradle.api.Transformer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

@CompileStatic
final class BackgroundPropertyImpl implements BackgroundProvider, Property<Background> {
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

  @Inject
  BackgroundPropertyImpl(ObjectFactory objectFactory, ProviderFactory providerFactory) {
    this.@background = objectFactory.property(Color)
    this.@providerFactory = providerFactory
    this.@provider = providerFactory.provider {
      (Background)new BackgroundImpl(background.get())
    }
  }

  @Delegate
  private final Provider<Background> provider

  private static final Transformer<Color, Background> COLOR_TRANSFORMER = new Transformer<Color, Background>() {
    @Override
    Color transform(Background backgroundConfiguration) {
      backgroundConfiguration.background
    }
  }

  @Override
  void set(@Nullable Background backgroundConfiguration) {
    value backgroundConfiguration
  }

  @Override
  void set(Provider<? extends Background> provider) {
    this.@background.set provider.map(COLOR_TRANSFORMER)
  }

  @Override
  Property<Background> value(@Nullable Background backgroundConfiguration) {
    this.@background.set backgroundConfiguration?.background
    this
  }

  @Override
  Property<Background> convention(Background backgroundConfiguration) {
    this.@background.convention backgroundConfiguration.background
    this
  }

  @Override
  Property<Background> convention(Provider<? extends Background> provider) {
    this.@background.convention provider.map(COLOR_TRANSFORMER)
    this
  }

  @Override
  void finalizeValue() {
    this.@background.finalizeValue()
  }
}
