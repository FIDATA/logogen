// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen

import groovy.transform.CompileStatic
import javax.inject.Inject
import org.fidata.logogen.shared.enums.HebrewLogoGenerationMethod
import org.fidata.logogen.shared.enums.RtlLogoGenerationMethod
import org.fidata.logogen.shared.properties.ConfigurableBackground
import org.fidata.logogen.shared.properties.ConfigurableDefault
import org.fidata.logogen.shared.properties.ConfigurableHebrew
import org.fidata.logogen.shared.properties.ConfigurableName
import org.fidata.logogen.shared.properties.ConfigurableRtl
import org.fidata.logogen.shared.properties.impl.BackgroundPropertyImpl
import org.fidata.logogen.shared.properties.impl.DefaultPropertyImpl
import org.fidata.logogen.shared.properties.impl.HebrewPropertyImpl
import org.fidata.logogen.shared.properties.impl.NamePropertyImpl
import org.fidata.logogen.shared.properties.impl.RtlPropertyImpl
import org.gradle.api.Project
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory

/**
 * logogen extension for {@link Project}
 */
@CompileStatic
final class LogoGenExtension {
  @Delegate
  private final ConfigurableDefault defaultProvider
  @Delegate
  private final ConfigurableRtl rtlProvider
  @Delegate
  private final ConfigurableHebrew hebrewProvider
  @Delegate
  private final ConfigurableBackground backgroundProvider
  @Delegate
  private final ConfigurableName nameProvider

  /**
   * Construct new LogoGenExtension object
   *
   * @param providerFactory {@link ProviderFactory} instance
   * @param objectFactory {@link ObjectFactory} instance
   */
  @Inject
  protected LogoGenExtension(ProviderFactory providerFactory, ObjectFactory objectFactory, ProjectLayout projectLayout) {
    this.@defaultProvider = new DefaultPropertyImpl(objectFactory, providerFactory, projectLayout)
    this.@rtlProvider = new RtlPropertyImpl(objectFactory, providerFactory, projectLayout)
    this.@hebrewProvider = new HebrewPropertyImpl(objectFactory, providerFactory, projectLayout)
    this.@backgroundProvider = new BackgroundPropertyImpl(objectFactory, providerFactory)
    this.@nameProvider = new NamePropertyImpl(objectFactory, providerFactory)

    rtlLogoGenerationMethod.convention providerFactory.provider {
      rtlSrcFile.present ? RtlLogoGenerationMethod.SEPARATE_SOURCE : RtlLogoGenerationMethod.MIRROW
    }
    hebrewLogoGenerationMethod.convention providerFactory.provider {
      hebrewSrcFile.present ? HebrewLogoGenerationMethod.SEPARATE_SOURCE : HebrewLogoGenerationMethod.STANDARD_RTL
    }
  }
}
