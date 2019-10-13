// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.shared.properties.impl

import groovy.transform.CompileStatic
import javax.annotation.Nullable
import javax.inject.Inject
import org.fidata.logogen.shared.configurations.Hebrew
import org.fidata.logogen.shared.configurations.impl.HebrewImpl
import org.fidata.logogen.shared.enums.HebrewLogoGenerationMethod
import org.fidata.logogen.shared.properties.ConfigurableDefault
import org.fidata.logogen.shared.properties.ConfigurableHebrew
import org.fidata.logogen.shared.properties.ConfigurableRtl
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
final class HebrewPropertyImpl implements ConfigurableHebrew, Property<Hebrew> {
  @Delegate(includeTypes = [ConfigurableDefault, ConfigurableRtl], interfaces = false)
  private final RtlPropertyImpl configurableRtl
  final RegularFileProperty hebrewSrcFile
  final Property<HebrewLogoGenerationMethod> hebrewLogoGenerationMethod

  @Delegate
  private final Provider<Hebrew> provider

  @Inject
  HebrewPropertyImpl(ProviderFactory providerFactory, ObjectFactory objectFactory, ProjectLayout projectLayout) {
    this.@configurableRtl = new RtlPropertyImpl(providerFactory, objectFactory, projectLayout)
    this.@hebrewSrcFile = objectFactory.fileProperty()
    this.@hebrewLogoGenerationMethod = objectFactory.property(HebrewLogoGenerationMethod)
    this.@provider = providerFactory.provider {
      (Hebrew)new HebrewImpl(configurableRtl.srcFile.get().asFile, configurableRtl.rtlSrcFile.get().asFile, configurableRtl.rtlLogoGenerationMethod.get(), hebrewSrcFile.get().asFile, hebrewLogoGenerationMethod.get())
    }
  }

  // WORKAROUND: @Delegate doesn't support both includes and includeTypes
  ProviderFactory getProviderFactory() {
    this.@configurableRtl.providerFactory
  }

  ProjectLayout getProjectLayout() {
    this.@configurableRtl.projectLayout
  }

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
  void set(@Nullable Hebrew hebrew) {
    value hebrew
  }

  @Override
  void set(Provider<? extends Hebrew> provider) {
    this.@hebrewSrcFile.set projectLayout.file(provider.map(HEBREW_SRC_FILE_TRANSFORMER))
    this.@hebrewLogoGenerationMethod.set provider.map(HEBREW_LOGO_GENERATION_METHOD_TRANSFORMER)
  }

  @Override
  Property<Hebrew> value(@Nullable Hebrew hebrew) {
    this.@configurableRtl.value hebrew
    this.@hebrewSrcFile.set hebrew?.hebrewSrcFile
    this.@hebrewLogoGenerationMethod.set hebrew?.hebrewLogoGenerationMethod
    this
  }

  @Override
  Property<Hebrew> convention(Hebrew hebrew) {
    this.@configurableRtl.convention hebrew
    this.@hebrewSrcFile.convention projectLayout.file(providerFactory.provider { hebrew.hebrewSrcFile })
    this.@hebrewLogoGenerationMethod.convention providerFactory.provider { hebrew.hebrewLogoGenerationMethod }
    this
  }

  @Override
  Property<Hebrew> convention(Provider<? extends Hebrew> provider) {
    this.@configurableRtl.convention provider
    this.@hebrewSrcFile.convention projectLayout.file(provider.map(HEBREW_SRC_FILE_TRANSFORMER))
    this.@hebrewLogoGenerationMethod.convention provider.map(HEBREW_LOGO_GENERATION_METHOD_TRANSFORMER)
    this
  }

  @Override
  void finalizeValue() {
    this.@configurableRtl.finalizeValue()
    this.@hebrewSrcFile.finalizeValue()
    this.@hebrewLogoGenerationMethod.finalizeValue()
  }
}
