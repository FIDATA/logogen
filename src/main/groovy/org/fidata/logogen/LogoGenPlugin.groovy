#!/usr/bin/env groovy
/*
 * org.fidata.logogen Gradle Project plugin
 * Copyright ©  Basil Peace
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
import org.fidata.logogen.generators.Generator
import org.fidata.logogen.shared.WithBackground
import org.fidata.logogen.shared.WithHebrew
import org.fidata.logogen.shared.HebrewLogoGenerationMethod
import org.fidata.logogen.shared.WithName
import org.fidata.logogen.shared.WithRtl
import org.fidata.logogen.shared.RtlLogoGenerationMethod
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Zip
import org.gradle.language.base.plugins.LifecycleBasePlugin

/**
 * org.fidata.logogen Gradle Project plugin
 */
@CompileStatic
final class LogoGenPlugin implements Plugin<Project> {
  /**
   * Name of logogen extension for {@link Project}
   */
  public static final String EXTENSION_NAME = 'logogen'

  public static final String CONFIGURATION_NAME = 'archives' // TODO / TOTHINK

  public static final String CONVERTED_OUTPUT_DIR_NAME = 'logogen/converted'

  public static final String PACKED_OUTPUT_DIR_NAME = 'logogen/packed'

  /**
   * {@inheritDoc}
   */
  @Override
  void apply(Project project) {
    project.plugins.apply LifecycleBasePlugin
    project.plugins.apply LogoGenBasePlugin

    project.configurations.maybeCreate(CONFIGURATION_NAME)

    LogoGenExtension extension = project.extensions.create(EXTENSION_NAME, LogoGenExtension)
    extension.logoName.convention(project.providers.provider {
      project.group.toString()
    })

    Provider<Directory> convertedOutputDirProvider = project.layout.buildDirectory.dir(CONVERTED_OUTPUT_DIR_NAME)

    Provider<Directory> packedOutputDirProvider = project.layout.buildDirectory.dir(PACKED_OUTPUT_DIR_NAME)

    project.plugins.withType(Generator).each { Generator generator ->
      TaskProvider<Generator.Converter> converterProvider = null
      if (generator.converterImplementationClass != null) {
        converterProvider = project.tasks.register(generator.name, generator.converterImplementationClass) { Generator.Converter converter ->
          converter.group = LifecycleBasePlugin.BUILD_GROUP
          converter.outputDir.set project.providers.provider { convertedOutputDirProvider.get().dir(generator.name) }

          // Default
          converter.srcFile.convention extension.srcFile

          if (WithRtl.isInstance(converter)) {
            WithRtl converterWithRtl = (WithRtl) converter
            converterWithRtl.rtlSrcFile.convention extension.rtlSrcFile
            converterWithRtl.rtlLogoGenerationMethod.convention project.providers.provider {
              converterWithRtl.rtlSrcFile.present
                ? RtlLogoGenerationMethod.SEPARATE_SOURCE
                : extension.rtlLogoGenerationMethod.get() != RtlLogoGenerationMethod.SEPARATE_SOURCE
                ? extension.rtlLogoGenerationMethod.get()
                : RtlLogoGenerationMethod.MIRROW
            }
          }

          if (WithHebrew.isInstance(converter)) {
            WithHebrew converterWithHebrew = (WithHebrew) converter
            converterWithHebrew.hebrewSrcFile.convention extension.hebrewSrcFile
            converterWithHebrew.hebrewLogoGenerationMethod.convention project.providers.provider {
              converterWithHebrew.hebrewSrcFile.present
                ? HebrewLogoGenerationMethod.SEPARATE_SOURCE
                : extension.hebrewLogoGenerationMethod.get() != HebrewLogoGenerationMethod.SEPARATE_SOURCE
                ? extension.hebrewLogoGenerationMethod.get()
                : HebrewLogoGenerationMethod.STANDARD_RTL
            }
          }

          if (WithName.isInstance(converter)) {
            WithName converterWithName = (WithName) converter
            converterWithName.logoName.convention extension.logoName
          }

          if (WithBackground.isInstance(converter)) {
            WithBackground converterWithBackground = (WithBackground) converter
            converterWithBackground.background.convention extension.background
          }
        }
      }

      generator.getOutputs(converterProvider ?: project.providers.provider { extension } /* TODO */).each { String classifier, CopySpec output ->
        TaskProvider<Zip> packProvider = project.tasks.register("pack${ generator.name.capitalize() }${ classifier?.capitalize() }" /* TODO */, Zip) { Zip pack ->
          pack.destinationDirectory.set packedOutputDirProvider
          pack.archiveBaseName.set generator.name
          pack.archiveClassifier.set classifier
          pack.with output
          if (converterProvider != null) {
            pack.dependsOn converterProvider
          }
        }

        project.artifacts.add CONFIGURATION_NAME, packProvider // TODO: .get() ?
      }
    }
  }
}
