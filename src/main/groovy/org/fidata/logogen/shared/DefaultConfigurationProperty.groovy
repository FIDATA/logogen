// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.shared

import groovy.transform.CompileStatic
import javax.annotation.Nullable
import javax.inject.Inject
import org.gradle.api.Transformer
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

@CompileStatic
class DefaultConfigurationProperty implements Default, Property<DefaultConfiguration> {
  protected final ProjectLayout projectLayout
  protected final ProviderFactory providerFactory
  final RegularFileProperty srcFile

  @Inject
  DefaultConfigurationProperty(ObjectFactory objectFactory, ProviderFactory providerFactory, ProjectLayout projectLayout) {
    this.@srcFile = objectFactory.fileProperty()
    this.@provider = providerFactory.provider {
      configuration
    }
    this.@projectLayout = projectLayout
    this.@providerFactory = providerFactory
  }

  protected DefaultConfiguration getConfiguration() {
    new DefaultConfiguration(srcFile.get().asFile)
  }

  protected final Provider<? extends DefaultConfiguration> provider

  @Delegate
  protected Provider<DefaultConfiguration> getDelegatedProvider() {
    this.@provider
  }

  private Transformer<File, DefaultConfiguration> srcFileTransformer = { DefaultConfiguration defaultConfiguration -> // TOTEST
    defaultConfiguration.srcFile
  }

  @Override
  final void set(@Nullable DefaultConfiguration defaultConfiguration) {
    value defaultConfiguration
  }

  @Override
  void set(Provider<? extends DefaultConfiguration> provider) {
    this.@srcFile.set projectLayout.file(provider.map(srcFileTransformer))
  }

  @Override
  Property<DefaultConfiguration> value(@Nullable DefaultConfiguration defaultConfiguration) {
    this.@srcFile.set defaultConfiguration?.srcFile
    this
  }

  @Override
  Property<DefaultConfiguration> convention(DefaultConfiguration defaultConfiguration) {
    this.@srcFile.convention projectLayout.file(providerFactory.provider { defaultConfiguration.srcFile })
    this
  }

  @Override
  Property<DefaultConfiguration> convention(Provider<? extends DefaultConfiguration> provider) {
    this.@srcFile.convention projectLayout.file(provider.map(srcFileTransformer))
    this
  }

  @Override
  void finalizeValue() {
    this.@srcFile.finalizeValue()
  }
}
