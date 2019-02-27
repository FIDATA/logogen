#!/usr/bin/env groovy
/*
 * org.fidata.logogen-base Gradle Project plugin
 * Copyright © 2015, 2018-2019  Basil Peace
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

import com.google.common.collect.ImmutableList
import groovy.transform.CompileStatic
import org.fidata.logogen.generators.*
import org.fidata.logogen.shared.HebrewLogoGenerationMethod
import org.fidata.logogen.shared.RtlLogoGenerationMethod
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
    Android1_0,
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
    WindowsTilesForPinnedWebsite,
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
        ((ExtensionAware)extension).extensions.create(generator.name, generator.extensionClass)
      }

      // TOTHINK
      // Class<? extends Generator.Converter> converterImplementationClass = generator.converterImplementationClass
      // project.extensions.extraProperties[converterImplementationClass.simpleName] = converterImplementationClass
    }
  }
}
