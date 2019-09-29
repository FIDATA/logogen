// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen

import com.google.common.collect.ImmutableList
import groovy.transform.CompileStatic
/*import org.fidata.logogen.generators.drafts.Android1_0
import org.fidata.logogen.generators.drafts.Android1_6
import org.fidata.logogen.generators.drafts.Android4_3
import org.fidata.logogen.generators.drafts.Facebook
import org.fidata.logogen.generators.drafts.Favicon
import org.fidata.logogen.generators.drafts.FreeDesktopGenerator*/
import org.fidata.logogen.generators.Generator
/*import org.fidata.logogen.generators.drafts.GitHub
import org.fidata.logogen.generators.drafts.GooglePlay
import org.fidata.logogen.generators.drafts.GooglePlus
import org.fidata.logogen.generators.drafts.Gravatar
import org.fidata.logogen.generators.drafts.Ios
import org.fidata.logogen.generators.drafts.Ios6
import org.fidata.logogen.generators.drafts.LaunchPad
import org.fidata.logogen.generators.drafts.Odnoklassniki
import org.fidata.logogen.generators.drafts.OpenHub
import org.fidata.logogen.generators.drafts.Osx
import org.fidata.logogen.generators.drafts.Twitter
import org.fidata.logogen.generators.drafts.VKontakte
import org.fidata.logogen.generators.drafts.Webclips
import org.fidata.logogen.generators.drafts.WindowsMainIcon
import org.fidata.logogen.generators.drafts.WindowsPhone
import org.fidata.logogen.generators.drafts.WindowsStore
import org.fidata.logogen.generators.drafts.WindowsTiles
import org.fidata.logogen.generators.drafts.WindowsTilesForDesktopApp
import org.fidata.logogen.generators.drafts.WindowsTilesForPinnedWebsite*/
import org.fidata.logogen.shared.enums.HebrewLogoGenerationMethod
import org.fidata.logogen.shared.enums.RtlLogoGenerationMethod
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

/**
 * org.fidata.logogen-base Gradle Project plugin
 */
@CompileStatic
final class LogoGenBasePlugin implements Plugin<Project> {
  /**
   * Name of logoGenerators extension for {@link Project}
   */
  public static final String EXTENSION_NAME = 'logoGenerators'

  private static final List<Class<Generator>> DEFAULT_GENERATORS = ImmutableList.copyOf([
    /*Android1_0,
    Android1_6,
    Android4_3,
    Facebook,
    Favicon,
    FreeDesktopGenerator.FreeDesktop,
    GitHub,
    GooglePlay,
    GooglePlus,
    Gravatar,
    Ios,
    Ios6,
    LaunchPad,
    Odnoklassniki,
    OpenHub,
    Osx,
    Twitter,
    VKontakte,
    Webclips,
    WindowsMainIcon,
    WindowsPhone,
    WindowsStore,
    WindowsTiles,
    WindowsTilesForDesktopApp,
    WindowsTilesForPinnedWebsite,*/
  ])

  /**
   * {@inheritDoc}
   */
  @Override
  void apply(Project project) {
    LogoGeneratorsExtension extension = project.extensions.create(EXTENSION_NAME, LogoGeneratorsExtension)

    project.extensions.extraProperties[RtlLogoGenerationMethod.simpleName] = RtlLogoGenerationMethod
    project.extensions.extraProperties[HebrewLogoGenerationMethod.simpleName] = HebrewLogoGenerationMethod

    project.plugins.withType(Generator).each { Generator generator ->
      if (generator.extensionClass != null) {
        /*Object generatorExtension =*/ extension.extensions.create(generator.name, generator.extensionClass)
        // TODO generator.convention extension
      }

      // TOTHINK
      // Class<? extends Generator.Converter> converterImplementationClass = generator.converterImplementationClass
      // project.extensions.extraProperties[converterImplementationClass.simpleName] = converterImplementationClass
    }
  }
}
