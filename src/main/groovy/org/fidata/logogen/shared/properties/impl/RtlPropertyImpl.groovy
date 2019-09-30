// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.shared.properties.impl

import groovy.transform.CompileStatic
import javax.annotation.Nullable
import javax.inject.Inject
import org.fidata.logogen.shared.configurations.Rtl
import org.fidata.logogen.shared.configurations.impl.RtlImpl
import org.fidata.logogen.shared.enums.RtlLogoGenerationMethod
import org.fidata.logogen.shared.properties.ConfigurableDefault
import org.fidata.logogen.shared.properties.ConfigurableRtl
import org.fidata.utils.ImmutableWithCustomConstructors
import org.gradle.api.Transformer
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

@ImmutableWithCustomConstructors
@CompileStatic
final class RtlPropertyImpl implements ConfigurableRtl, Property<Rtl> {
  @Delegate(includeTypes = [ConfigurableDefault])
  private final DefaultPropertyImpl configurableDefault
  final RegularFileProperty rtlSrcFile
  final Property<RtlLogoGenerationMethod> rtlLogoGenerationMethod

  @Delegate
  private final Provider<Rtl> provider

  @Inject
  RtlPropertyImpl(ProviderFactory providerFactory, ObjectFactory objectFactory, ProjectLayout projectLayout) {
    this.@configurableDefault = new DefaultPropertyImpl(providerFactory, objectFactory, projectLayout)
    this.@rtlSrcFile = objectFactory.fileProperty()
    this.@rtlLogoGenerationMethod = objectFactory.property(RtlLogoGenerationMethod)
    this.@provider = providerFactory.provider {
      (Rtl)new RtlImpl(configurableDefault.srcFile.get().asFile, rtlSrcFile.get().asFile, rtlLogoGenerationMethod.get())
    }
  }

  // WORKAROUND: @Delegate doesn't support both includes and includeTypes
  ProviderFactory getProviderFactory() {
    this.@configurableDefault.providerFactory
  }

  ProjectLayout getProjectLayout() {
    this.@configurableDefault.projectLayout
  }

  private static final Transformer<File, Rtl> RTL_SRC_FILE_TRANSFORMER = new Transformer<File, Rtl>() {
    @Override
    File transform(Rtl rtlConfiguration) {
      rtlConfiguration.rtlSrcFile
    }
  }

  private static final Transformer<RtlLogoGenerationMethod, Rtl> RTL_LOGO_GENERATION_METHOD_TRANSFORMER = new Transformer<RtlLogoGenerationMethod, Rtl>() {
    @Override
    RtlLogoGenerationMethod transform(Rtl rtl) {
      rtl.rtlLogoGenerationMethod
    }
  }

  @Override
  void set(@Nullable Rtl rtlConfiguration) {
    value rtlConfiguration
  }

  @Override
  void set(Provider<? extends Rtl> provider) {
    this.@configurableDefault.set provider
    this.@rtlSrcFile.set projectLayout.file(provider.map(RTL_SRC_FILE_TRANSFORMER))
    this.@rtlLogoGenerationMethod.set provider.map(RTL_LOGO_GENERATION_METHOD_TRANSFORMER)
  }

  @Override
  Property<Rtl> value(@Nullable Rtl rtl) {
    this.@configurableDefault.value rtl
    this.@rtlSrcFile.set rtl?.rtlSrcFile
    this.@rtlLogoGenerationMethod.set rtl?.rtlLogoGenerationMethod
    this
  }

  @Override
  Property<Rtl> convention(Rtl rtl) {
    this.@configurableDefault.convention rtl
    this.@rtlSrcFile.convention projectLayout.file(providerFactory.provider { rtl.rtlSrcFile })
    this.@rtlLogoGenerationMethod.convention providerFactory.provider { rtl.rtlLogoGenerationMethod }
    this
  }

  @Override
  Property<Rtl> convention(Provider<? extends Rtl> provider) {
    this.@configurableDefault.convention provider
    this.@rtlSrcFile.convention projectLayout.file(provider.map(RTL_SRC_FILE_TRANSFORMER))
    this.@rtlLogoGenerationMethod.convention provider.map(RTL_LOGO_GENERATION_METHOD_TRANSFORMER)
    this
  }

  @Override
  void finalizeValue() {
    this.@configurableDefault.finalizeValue()
    this.@rtlSrcFile.finalizeValue()
    this.@rtlLogoGenerationMethod.finalizeValue()
  }
}
