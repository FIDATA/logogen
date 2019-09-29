// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.shared

import groovy.transform.CompileStatic
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

@CompileStatic
class HebrewConfigurationProperty extends RtlConfigurationProperty implements WithHebrew, Property<HebrewConfiguration> {
  final RegularFileProperty hebrewSrcFile

  final Property<HebrewLogoGenerationMethod> hebrewLogoGenerationMethod

  HebrewConfigurationProperty(ObjectFactory objectFactory, ProviderFactory providerFactory, ProjectLayout projectLayout) {
    super(objectFactory, providerFactory)
    this.@hebrewSrcFile = objectFactory.fileProperty()
    this.@hebrewLogoGenerationMethod = objectFactory.property(HebrewLogoGenerationMethod)
  }

  @Override
  protected HebrewConfiguration getConfiguration() {
    new HebrewConfiguration(srcFile.get().asFile, rtlSrcFile.get().asFile, rtlLogoGenerationMethod.get(), hebrewSrcFile.get().asFile, hebrewLogoGenerationMethod.get())
  }

  @Delegate
  @Override
  protected Provider<HebrewConfiguration> getDelegatedProvider() {
    (Provider<HebrewConfiguration>)super.provider
  }

  private Transformer<File, HebrewConfiguration> hebrewSrcFileTransformer = { HebrewConfiguration hebrewConfiguration -> // TOTEST
    hebrewConfiguration.hebrewSrcFile
  }

  private Transformer<File, HebrewConfiguration> hebrewLogoGenerationMethodTransformer = { HebrewConfiguration hebrewConfiguration -> // TOTEST
    hebrewConfiguration.hebrewLogoGenerationMethod
  }

  @Override
  void set(Provider<? extends HebrewConfiguration> provider) {
    super.set provider
    this.@hebrewSrcFile.set projectLayout.file(provider.map(hebrewSrcFileTransformer))
    this.@hebrewLogoGenerationMethod.set provider.map(hebrewLogoGenerationMethodTransformer)
  }

  @Override
  Property<HebrewConfiguration> value(@Nullable HebrewConfiguration hebrewConfiguration) {
    super.value convention
    this.@hebrewSrcFile.set hebrewConfiguration?.hebrewSrcFile
    this.@hebrewLogoGenerationMethod.set hebrewConfiguration?.hebrewLogoGenerationMethod
    this
  }

  @Override
  Property<HebrewConfiguration> convention(HebrewConfiguration hebrewConfiguration) {
    super.convention convention
    this.@hebrewSrcFile.convention projectLayout.file(providerFactory.provider { hebrewConfiguration.hebrewSrcFile })
    this.@hebrewLogoGenerationMethod.convention providerFactory.provider { hebrewConfiguration.hebrewLogoGenerationMethod }
    this
  }

  @Override
  Property<HebrewConfiguration> convention(Provider<? extends HebrewConfiguration> provider) {
    super.convention convention
    this.@hebrewSrcFile.convention projectLayout.file(provider.map(hebrewSrcFileTransformer))
    this.@hebrewLogoGenerationMethod.convention provider.map(hebrewLogoGenerationMethodTransformer)
    this
  }
}
