// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.shared.properties.impl

import groovy.transform.CompileStatic
import javax.annotation.Nullable
import javax.inject.Inject
import org.fidata.logogen.shared.configurations.Rtl
import org.fidata.logogen.shared.configurations.impl.RtlImpl
import org.fidata.logogen.shared.enums.RtlLogoGenerationMethod
import org.fidata.logogen.shared.providers.RtlProvider
import org.gradle.api.Transformer
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

@CompileStatic
final class RtlPropertyImpl implements RtlProvider, Property<Rtl> {
  protected final ProjectLayout projectLayout
  protected final ProviderFactory providerFactory

  final RegularFileProperty rtlSrcFile

  final Property<RtlLogoGenerationMethod> rtlLogoGenerationMethod

  @Inject
  RtlPropertyImpl(ObjectFactory objectFactory, ProviderFactory providerFactory, ProjectLayout projectLayout) {
    this.@rtlSrcFile = objectFactory.fileProperty()
    this.@rtlLogoGenerationMethod = objectFactory.property(RtlLogoGenerationMethod)
    this.@providerFactory = providerFactory
    this.@projectLayout = projectLayout
    this.@provider = providerFactory.provider {
      (Rtl)new RtlImpl(rtlSrcFile.get().asFile, rtlLogoGenerationMethod.get())
    }
  }

  @Delegate
  private final Provider<Rtl> provider

  private static final Transformer<File, Rtl> RTL_SRC_FILE_TRANSFORMER = new Transformer<File, Rtl>() {
    @Override
    File transform(Rtl rtlConfiguration) {
      rtlConfiguration.rtlSrcFile
    }
  }

  private static final Transformer<RtlLogoGenerationMethod, Rtl> RTL_LOGO_GENERATION_METHOD_TRANSFORMER = new Transformer<RtlLogoGenerationMethod, Rtl>() {
    @Override
    RtlLogoGenerationMethod transform(Rtl rtlConfiguration) {
      rtlConfiguration.rtlLogoGenerationMethod
    }
  }

  @Override
  void set(Provider<? extends Rtl> provider) {
    this.@rtlSrcFile.set projectLayout.file(provider.map(RTL_SRC_FILE_TRANSFORMER))
    this.@rtlLogoGenerationMethod.set provider.map(RTL_LOGO_GENERATION_METHOD_TRANSFORMER)
  }

  @Override
  void set(@Nullable Rtl rtlConfiguration) {
    value rtlConfiguration
  }

  @Override
  Property<Rtl> value(@Nullable Rtl rtlConfiguration) {
    this.@rtlSrcFile.set rtlConfiguration?.rtlSrcFile
    this.@rtlLogoGenerationMethod.set rtlConfiguration?.rtlLogoGenerationMethod
    this
  }

  @Override
  Property<Rtl> convention(Rtl rtlConfiguration) {
    this.@rtlSrcFile.convention projectLayout.file(providerFactory.provider { rtlConfiguration.rtlSrcFile })
    this.@rtlLogoGenerationMethod.convention providerFactory.provider { rtlConfiguration.rtlLogoGenerationMethod }
    this
  }

  @Override
  Property<Rtl> convention(Provider<? extends Rtl> provider) {
    this.@rtlSrcFile.convention projectLayout.file(provider.map(RTL_SRC_FILE_TRANSFORMER))
    this.@rtlLogoGenerationMethod.convention provider.map(RTL_LOGO_GENERATION_METHOD_TRANSFORMER)
    this
  }

  @Override
  void finalizeValue() {
    this.@rtlSrcFile.finalizeValue()
    this.@rtlLogoGenerationMethod.finalizeValue()
  }
}
