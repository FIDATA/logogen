// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.shared

import groovy.transform.CompileStatic
import javax.annotation.Nullable
import org.gradle.api.Transformer
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

@CompileStatic
class RtlConfigurationProperty extends RtlConfigurationProperty implements WithRtl, Property<RtlConfiguration> {
  final RegularFileProperty rtlSrcFile

  final Property<RtlLogoGenerationMethod> rtlLogoGenerationMethod

  RtlConfigurationProperty(ObjectFactory objectFactory, ProviderFactory providerFactory, ProjectLayout projectLayout) {
    super(objectFactory, providerFactory, projectLayout)
    this.@rtlSrcFile = objectFactory.fileProperty()
    this.@rtlLogoGenerationMethod = objectFactory.property(RtlLogoGenerationMethod)
  }

  @Override
  protected RtlConfiguration getConfiguration() {
    new RtlConfiguration(srcFile.get().asFile, rtlSrcFile.get().asFile, rtlLogoGenerationMethod.get())
  }

  @Delegate
  @Override
  protected Provider<RtlConfiguration> getDelegatedProvider() {
    (Provider<RtlConfiguration>)super.provider
  }

  private Transformer<File, RtlConfiguration> rtlSrcFileTransformer = { RtlConfiguration rtlConfiguration -> // TOTEST
    rtlConfiguration.rtlSrcFile
  }

  private Transformer<File, RtlConfiguration> rtlLogoGenerationMethodTransformer = { RtlConfiguration rtlConfiguration -> // TOTEST
    rtlConfiguration.rtlLogoGenerationMethod
  }

  @Override
  void set(Provider<? extends RtlConfiguration> provider) {
    super.set provider
    this.@rtlSrcFile.set projectLayout.file(provider.map(rtlSrcFileTransformer))
    this.@rtlLogoGenerationMethod.set provider.map(rtlLogoGenerationMethodTransformer)
  }

  @Override
  Property<RtlConfiguration> value(@Nullable RtlConfiguration rtlConfiguration) {
  	super.value convention
    this.@rtlSrcFile.set rtlConfiguration?.rtlSrcFile
    this.@rtlLogoGenerationMethod.set rtlConfiguration?.rtlLogoGenerationMethod
    this
  }

  @Override
  Property<RtlConfiguration> convention(RtlConfiguration rtlConfiguration) {
  	super.convention convention
    this.@rtlSrcFile.convention projectLayout.file(providerFactory.provider { rtlConfiguration.rtlSrcFile })
    this.@rtlLogoGenerationMethod.convention providerFactory.provider { rtlConfiguration.rtlLogoGenerationMethod }
    this
  }

  @Override
  Property<RtlConfiguration> convention(Provider<? extends RtlConfiguration> provider) {
  	super.convention convention
    this.@rtlSrcFile.convention projectLayout.file(provider.map(rtlSrcFileTransformer))
    this.@rtlLogoGenerationMethod.convention provider.map(rtlLogoGenerationMethodTransformer)
    this
  }

  @Override
  void finalizeValue() {
  	super.finalizeValue()
    this.@rtlSrcFile.finalizeValue()
    this.@rtlSrcFile.finalizeValue()
  }
}
