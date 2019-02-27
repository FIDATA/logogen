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
import org.fidata.logogen.shared.RtlLogoGenerationMethod
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectFactory
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

  private NamedDomainObjectContainer<LogoGeneratorDescriptor> generators

  /**
   * Collection of descriptors of available generators.
   * Custom generators could be registered here from build script or by custom plugin.
   * If {@code org.fidata.logogen} plugin is applied,
   * it will create a task for each registered generator.
   *
   * Removing items from this collection is supported, but discouraged.
   * If an already registered generator is removed
   * then the corresponding task will remain created, but will be disabled
   *
   * @return collection of generators
   */
  NamedDomainObjectContainer<LogoGeneratorDescriptor> getGenerators() {
    this.@generators
  }

  private static final List<LogoGeneratorDescriptor> DEFAULT_GENERATORS = ImmutableList.copyOf([
    Android1_0.DESCRIPTOR,
    Android1_6.DESCRIPTOR,
    Android4_3.DESCRIPTOR,
    Facebook.DESCRIPTOR,
    Favicon.DESCRIPTOR,
    FreeDesktop.DESCRIPTOR,
    GitHub.DESCRIPTOR,
    GooglePlay.DESCRIPTOR,
    GooglePlus.DESCRIPTOR,
    Gravatar.DESCRIPTOR,
    Ios.DESCRIPTOR,
    Ios6.DESCRIPTOR,
    LaunchPad.DESCRIPTOR,
    Odnoklassniki.DESCRIPTOR,
    OpenHub.DESCRIPTOR,
    Osx.DESCRIPTOR,
    Twitter.DESCRIPTOR,
    VKontakte.DESCRIPTOR,
    Webclips.DESCRIPTOR,
    WindowsMainIcon.DESCRIPTOR,
    WindowsPhone.DESCRIPTOR,
    WindowsStore.DESCRIPTOR,
    WindowsTiles.DESCRIPTOR,
    WindowsTilesForDesktopApp.DESCRIPTOR,
    WindowsTilesForPinnedWebsite.DESCRIPTOR,
  ])

  /**
   * {@inheritDoc}
   */
  @Override
  void apply(Project project) {
    LogoGeneratorsExtension extension = project.extensions.create(EXTENSION_NAME, LogoGeneratorsExtension)

    this.@generators = project.container(LogoGeneratorDescriptor, (NamedDomainObjectFactory)null)

    project.extensions.extraProperties[RtlLogoGenerationMethod.simpleName] = RtlLogoGenerationMethod

    generators.configureEach { LogoGeneratorDescriptor descriptor ->
      if (descriptor.extensionClass != null) {
        ((ExtensionAware)extension).extensions.create(descriptor.name, descriptor.extensionClass)
      }

      Class<? extends Converter> logoGeneratorClass = descriptor.implementationClass
      project.extensions.extraProperties[logoGeneratorClass.simpleName] = logoGeneratorClass
    }

    generators.addAll DEFAULT_GENERATORS
  }
}
