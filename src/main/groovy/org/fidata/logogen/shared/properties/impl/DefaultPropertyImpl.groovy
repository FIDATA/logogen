// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.shared.properties.impl

import groovy.transform.CompileStatic
import javax.annotation.Nullable
import javax.inject.Inject
import org.fidata.logogen.shared.configurations.Default
import org.fidata.logogen.shared.configurations.impl.DefaultImpl
import org.fidata.logogen.shared.properties.ConfigurableDefault
import org.fidata.groovy.utils.ImmutableWithCustomConstructors
import org.gradle.api.Transformer
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

@ImmutableWithCustomConstructors
@CompileStatic
final class DefaultPropertyImpl implements ConfigurableDefault, Property<Default> {
  final ProjectLayout projectLayout
  final ProviderFactory providerFactory

  final RegularFileProperty srcFile

  @Delegate
  private final Provider<Default> provider

  @Inject
  DefaultPropertyImpl(ProviderFactory providerFactory, ObjectFactory objectFactory, ProjectLayout projectLayout) {
    this.@srcFile = objectFactory.fileProperty()
    this.@providerFactory = providerFactory
    this.@projectLayout = projectLayout
    this.@provider = providerFactory.provider {
      (Default)new DefaultImpl(srcFile.get().asFile)
    }
  }

  private static final Transformer<File, Default> SRC_FILE_TRANSFORMER = new Transformer<File, Default>() {
    @Override
    File transform(Default defaultConfiguration) {
      defaultConfiguration.srcFile
    }
  }

  @Override
  void set(@Nullable Default defaultConfiguration) {
    value defaultConfiguration
  }

  @Override
  void set(Provider<? extends Default> provider) {
    this.@srcFile.set projectLayout.file(provider.map(SRC_FILE_TRANSFORMER))
  }

  @Override
  Property<Default> value(@Nullable Default defaultConfiguration) {
    this.@srcFile.set defaultConfiguration?.srcFile
    this
  }

  @Override
  Property<Default> convention(Default defaultConfiguration) {
    this.@srcFile.convention projectLayout.file(providerFactory.provider { defaultConfiguration.srcFile })
    this
  }

  @Override
  Property<Default> convention(Provider<? extends Default> provider) {
    this.@srcFile.convention projectLayout.file(provider.map(SRC_FILE_TRANSFORMER))
    this
  }

  @Override
  void finalizeValue() {
    this.@srcFile.finalizeValue()
  }
}
