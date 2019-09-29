// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.shared.properties.impl

import groovy.transform.CompileStatic
import javax.annotation.Nullable
import javax.inject.Inject
import org.fidata.logogen.shared.configurations.Hebrew
import org.fidata.logogen.shared.configurations.impl.HebrewImpl
import org.fidata.logogen.shared.enums.HebrewLogoGenerationMethod
import org.fidata.logogen.shared.providers.HebrewProvider
import org.gradle.api.Transformer
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

@CompileStatic
final class HebrewPropertyImpl implements HebrewProvider, Property<Hebrew> {
  protected final ProjectLayout projectLayout
  protected final ProviderFactory providerFactory

  final RegularFileProperty hebrewSrcFile

  final Property<HebrewLogoGenerationMethod> hebrewLogoGenerationMethod

  @Inject
  HebrewPropertyImpl(ObjectFactory objectFactory, ProviderFactory providerFactory, ProjectLayout projectLayout) {
    this.@hebrewSrcFile = objectFactory.fileProperty()
    this.@hebrewLogoGenerationMethod = objectFactory.property(HebrewLogoGenerationMethod)
    this.@providerFactory = providerFactory
    this.@projectLayout = projectLayout
    this.@provider = providerFactory.provider {
      (Hebrew)new HebrewImpl(hebrewSrcFile.get().asFile, hebrewLogoGenerationMethod.get())
    }
  }

  @Delegate
  private final Provider<Hebrew> provider

  private static final Transformer<File, Hebrew> HEBREW_SRC_FILE_TRANSFORMER = new Transformer<File, Hebrew>() {
    @Override
    File transform(Hebrew hebrewConfiguration) {
      hebrewConfiguration.hebrewSrcFile
    }
  }

  private static final Transformer<HebrewLogoGenerationMethod, Hebrew> HEBREW_LOGO_GENERATION_METHOD_TRANSFORMER = new Transformer<HebrewLogoGenerationMethod, Hebrew>() {
    @Override
    HebrewLogoGenerationMethod transform(Hebrew hebrewConfiguration) {
      hebrewConfiguration.hebrewLogoGenerationMethod
    }
  }

  @Override
  void set(@Nullable Hebrew hebrewConfiguration) {
    value hebrewConfiguration
  }

  @Override
  void set(Provider<? extends Hebrew> provider) {
    this.@hebrewSrcFile.set projectLayout.file(provider.map(HEBREW_SRC_FILE_TRANSFORMER))
    this.@hebrewLogoGenerationMethod.set provider.map(HEBREW_LOGO_GENERATION_METHOD_TRANSFORMER)
  }

  @Override
  Property<Hebrew> value(@Nullable Hebrew hebrewConfiguration) {
    this.@hebrewSrcFile.set hebrewConfiguration?.hebrewSrcFile
    this.@hebrewLogoGenerationMethod.set hebrewConfiguration?.hebrewLogoGenerationMethod
    this
  }

  @Override
  Property<Hebrew> convention(Hebrew hebrewConfiguration) {
    this.@hebrewSrcFile.convention projectLayout.file(providerFactory.provider { hebrewConfiguration.hebrewSrcFile })
    this.@hebrewLogoGenerationMethod.convention providerFactory.provider { hebrewConfiguration.hebrewLogoGenerationMethod }
    this
  }

  @Override
  Property<Hebrew> convention(Provider<? extends Hebrew> provider) {
    this.@hebrewSrcFile.convention projectLayout.file(provider.map(HEBREW_SRC_FILE_TRANSFORMER))
    this.@hebrewLogoGenerationMethod.convention provider.map(HEBREW_LOGO_GENERATION_METHOD_TRANSFORMER)
    this
  }

  @Override
  void finalizeValue() {
    this.@hebrewSrcFile.finalizeValue()
    this.@hebrewLogoGenerationMethod.finalizeValue()
  }
}
