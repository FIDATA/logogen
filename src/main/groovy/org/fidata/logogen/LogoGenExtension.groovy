// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen

import groovy.transform.CompileStatic
import org.fidata.logogen.annotations.DelegateWithoutProviderInterface
import org.fidata.logogen.shared.HebrewLogoGenerationMethod
import org.fidata.logogen.shared.NameConfigurationProperty
import org.fidata.logogen.shared.BackgroundConfigurationProperty
import org.fidata.logogen.shared.HebrewConfigurationProperty
import org.fidata.logogen.shared.DefaultConfigurationProperty
import org.fidata.logogen.shared.RtlLogoGenerationMethod
import org.fidata.logogen.shared.RtlConfigurationProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

import org.gradle.api.Project

/**
 * logogen extension for {@link Project}
 */
@CompileStatic
final class LogoGenExtension {
  @DelegateWithoutProviderInterface // TODO
  private final DefaultConfigurationProperty defaultConfigurationProvider
  @DelegateWithoutProviderInterface
  private final RtlConfigurationProperty rtlConfigurationProvider
  @DelegateWithoutProviderInterface
  private final HebrewConfigurationProperty hebrewConfigurationProvider
  @DelegateWithoutProviderInterface
  private final BackgroundConfigurationProperty backgroundConfigurationProvider
  @DelegateWithoutProviderInterface
  private final NameConfigurationProperty nameConfigurationProvider

  /**
   * Construct new LogoGenExtension object
   *
   * @param providerFactory {@link ProviderFactory} instance
   * @param objectFactory {@link ObjectFactory} instance
   */
  @Inject
  protected LogoGenExtension(ProviderFactory providerFactory, ObjectFactory objectFactory) {
    this.@defaultConfigurationProvider = new DefaultConfigurationProperty(objectFactory)
    this.@rtlConfigurationProvider = new RtlConfigurationProperty(objectFactory)
    this.@hebrewConfigurationProvider = new HebrewConfigurationProperty(objectFactory)
    this.@backgroundConfigurationProvider = new BackgroundConfigurationProperty(objectFactory)
    this.@nameConfigurationProvider = new NameConfigurationProperty(objectFactory)

    rtlLogoGenerationMethod.convention providerFactory.provider {
      rtlSrcFile.present ? RtlLogoGenerationMethod.SEPARATE_SOURCE : RtlLogoGenerationMethod.MIRROW
    }
    hebrewLogoGenerationMethod.convention providerFactory.provider {
      hebrewSrcFile.present ? HebrewLogoGenerationMethod.SEPARATE_SOURCE : HebrewLogoGenerationMethod.STANDARD_RTL
    }
  }
}
