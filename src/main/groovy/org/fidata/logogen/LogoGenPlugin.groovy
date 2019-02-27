#!/usr/bin/env groovy
/*
 * org.fidata.logogen Gradle Project plugin
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

import groovy.transform.CompileStatic
import org.fidata.logogen.generators.Converter
import org.fidata.logogen.shared.Background
import org.fidata.logogen.shared.HebrewLogo
import org.fidata.logogen.shared.HebrewLogoGenerationMethod
import org.fidata.logogen.shared.LogoName
import org.fidata.logogen.shared.RtlLogo
import org.fidata.logogen.shared.RtlLogoGenerationMethod
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.tasks.TaskProvider
import org.gradle.language.base.plugins.LifecycleBasePlugin

/**
 * org.fidata.logogen Gradle Project plugin
 */
@CompileStatic
final class LogoGenPlugin implements Plugin<Project> {
  /**
   * Name of logogen root task
   */
  public static final String ROOT_TASK_NAME = 'logogen'

  /**
   * Name of logogen extension for {@link Project}
   */
  public static final String EXTENSION_NAME = 'logogen'

  /**
   * {@inheritDoc}
   */
  @Override
  void apply(Project project) {
    LogoGenBasePlugin basePlugin = project.plugins.apply(LogoGenBasePlugin)

    LogoGenExtension extension = project.extensions.create(EXTENSION_NAME, LogoGenExtension)
    extension.logoName.convention(project.providers.provider {
      project.group.toString()
    })

    TaskProvider<Task> rootTaskProvider = project.tasks.register(ROOT_TASK_NAME)
    project.plugins.withType(LifecycleBasePlugin) {
      project.tasks.named(LifecycleBasePlugin.ASSEMBLE_TASK_NAME).configure { Task assemble ->
        assemble.dependsOn rootTaskProvider
      }
    }

    basePlugin.generators.configureEach { LogoGeneratorDescriptor descriptor ->
      TaskProvider<Converter> logoGeneratorProvider = project.tasks.register(descriptor.name, descriptor.implementationClass) { Converter logoGenerator ->
        logoGenerator.group = LifecycleBasePlugin.BUILD_GROUP
        logoGenerator.outputDir.set project.layout.buildDirectory.dir(descriptor.name)
        // Logo
        logoGenerator.srcFile.convention extension.srcFile
        if (RtlLogo.isInstance(logoGenerator)) {
          RtlLogo rtlLogo = (RtlLogo)logoGenerator
          rtlLogo.rtlSrcFile.convention extension.rtlSrcFile
          rtlLogo.rtlLogoGenerationMethod.convention project.providers.provider {
            rtlLogo.rtlSrcFile.present
              ? RtlLogoGenerationMethod.SEPARATE_SOURCE
              : extension.rtlLogoGenerationMethod.get() != RtlLogoGenerationMethod.SEPARATE_SOURCE
              ? extension.rtlLogoGenerationMethod.get()
              : RtlLogoGenerationMethod.MIRROW
          }
        }
        if (HebrewLogo.isInstance(logoGenerator)) {
          HebrewLogo hebrewLogo = (HebrewLogo)logoGenerator
          hebrewLogo.hebrewSrcFile.convention extension.hebrewSrcFile
          hebrewLogo.hebrewLogoGenerationMethod.convention project.providers.provider {
            hebrewLogo.hebrewSrcFile.present
              ? HebrewLogoGenerationMethod.SEPARATE_SOURCE
              : extension.hebrewLogoGenerationMethod.get() != HebrewLogoGenerationMethod.SEPARATE_SOURCE
                ? extension.hebrewLogoGenerationMethod.get()
                : HebrewLogoGenerationMethod.STANDARD_RTL
          }
        }
        if (LogoName.isInstance(logoGenerator)) {
          LogoName logoName = (LogoName)logoGenerator
          logoName.logoName.convention extension.logoName
        }
        if (Background.isInstance(logoGenerator)) {
          Background background = (Background)logoGenerator
          background.background.convention extension.background
        }
      }
      rootTaskProvider.configure { Task rootTask ->
        rootTask.dependsOn logoGeneratorProvider
      }
      /*
        TODO:
        1. distributions {
          // main { contents {
          // 	from "$buildDir/main"
          // } }
          windows_mainicon { contents {
            from "$buildDir/windows_mainicon"
          } }
        2. ArtifactoryPublish & publications
       */
      // TODO: webclips ??
    }

    basePlugin.generators.whenObjectRemoved { LogoGeneratorDescriptor descriptor ->
      project.tasks.withType(descriptor.implementationClass).named(descriptor.name).configure { Converter logoGenerator ->
        logoGenerator.enabled = false
      }
    }

    new DslObject(project.extensions.getByType(LogoGenExtension)).extensions
  }
}
