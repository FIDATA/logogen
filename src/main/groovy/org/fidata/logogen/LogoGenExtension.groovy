// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen

import groovy.transform.CompileStatic
import javax.inject.Inject
import org.fidata.logogen.shared.enums.HebrewLogoGenerationMethod
import org.fidata.logogen.shared.enums.RtlLogoGenerationMethod
import org.fidata.logogen.shared.properties.ConfigurableBackground
import org.fidata.logogen.shared.properties.ConfigurableHebrew
import org.fidata.logogen.shared.properties.ConfigurableName
import org.fidata.logogen.shared.properties.impl.BackgroundPropertyImpl
import org.fidata.logogen.shared.properties.impl.HebrewPropertyImpl
import org.fidata.logogen.shared.properties.impl.NamePropertyImpl
import org.fidata.groovy.utils.ImmutableWithCustomConstructors
import org.gradle.api.Project
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory

/**
 * logogen extension for {@link Project}
 */
@ImmutableWithCustomConstructors
@CompileStatic
final class LogoGenExtension {
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
    this.@hebrewProvider = new HebrewPropertyImpl(providerFactory, objectFactory, projectLayout)
    this.@backgroundProvider = new BackgroundPropertyImpl(providerFactory, objectFactory)
    this.@nameProvider = new NamePropertyImpl(providerFactory, objectFactory)

    // TODO: the same code should be for separate generator extensions
    rtlLogoGenerationMethod.convention providerFactory.provider {
      rtlSrcFile.present ? RtlLogoGenerationMethod.SEPARATE_SOURCE : RtlLogoGenerationMethod.MIRROW
    }
    hebrewLogoGenerationMethod.convention providerFactory.provider {
      hebrewSrcFile.present ? HebrewLogoGenerationMethod.SEPARATE_SOURCE : HebrewLogoGenerationMethod.STANDARD_RTL
    }
  }
}
