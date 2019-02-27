#!/usr/bin/env groovy
/*
 * LogoGenExtension class
 * Copyright © 2019  Basil Peace
 *
 * This file is part of Logo Generator.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.fidata.logogen

import groovy.transform.CompileStatic
import org.fidata.logogen.annotations.DelegateWithoutProviderInterface
import org.fidata.logogen.shared.HebrewLogoGenerationMethod
import org.fidata.logogen.shared.LogoNameConfigurationProvider
import org.fidata.logogen.shared.BackgroundConfigurationProvider
import org.fidata.logogen.shared.HebrewLogoConfigurationProvider
import org.fidata.logogen.shared.LogoConfigurationProvider
import org.fidata.logogen.shared.RtlLogoGenerationMethod
import org.fidata.logogen.shared.RtlLogoConfigurationProvider
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

import org.gradle.api.Project

/**
 * logogen extension for {@link Project}
 */
@CompileStatic
final class LogoGenExtension {
  @DelegateWithoutProviderInterface
  private final LogoConfigurationProvider logoConfigurationProvider
  @DelegateWithoutProviderInterface
  private final RtlLogoConfigurationProvider rtlLogoConfigurationProvider
  @DelegateWithoutProviderInterface
  private final HebrewLogoConfigurationProvider hebrewLogoConfigurationProvider
  @DelegateWithoutProviderInterface
  private final BackgroundConfigurationProvider backgroundConfigurationProvider
  @DelegateWithoutProviderInterface
  private final LogoNameConfigurationProvider logoNameConfigurationProvider

  /**
   * Construct new LogoGenExtension object
   *
   * @param providerFactory {@link ProviderFactory} instance
   * @param objectFactory {@link ObjectFactory} instance
   */
  @Inject
  protected LogoGenExtension(ProviderFactory providerFactory, ObjectFactory objectFactory) {
    this.@logoConfigurationProvider = new LogoConfigurationProvider(objectFactory)
    this.@rtlLogoConfigurationProvider = new RtlLogoConfigurationProvider(objectFactory)
    this.@hebrewLogoConfigurationProvider = new HebrewLogoConfigurationProvider(objectFactory)
    this.@backgroundConfigurationProvider = new BackgroundConfigurationProvider(objectFactory)
    this.@logoNameConfigurationProvider = new LogoNameConfigurationProvider(objectFactory)

    rtlLogoGenerationMethod.convention providerFactory.provider {
      rtlSrcFile.present ? RtlLogoGenerationMethod.SEPARATE_SOURCE : RtlLogoGenerationMethod.MIRROW
    }
    hebrewLogoGenerationMethod.convention providerFactory.provider {
      hebrewSrcFile.present ? HebrewLogoGenerationMethod.SEPARATE_SOURCE : HebrewLogoGenerationMethod.STANDARD_RTL
    }
  }
}
