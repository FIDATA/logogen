#!/usr/bin/env groovy
/*
 * org.fidata.logogen Gradle plugin
 * Copyright © 2015, 2018  Basil Peace
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
import org.fidata.logogen.generators.LogoGenerator
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.language.base.plugins.LifecycleBasePlugin

@CompileStatic
final class LogoGenPlugin implements Plugin<Project> {
  public static final String ROOT_TASK_NAME = 'logogen'

  public static final String EXTENSION_NAME = 'logogen'

  @Override
  void apply(Project project) {
    project.plugins.apply LogoGenBasePlugin

    LogoGenExtension extension = project.extensions.create(EXTENSION_NAME, LogoGenExtension)

    TaskProvider<Task> rootTaskProvider = project.tasks.register(ROOT_TASK_NAME)
    project.plugins.withType(LifecycleBasePlugin) {
      project.tasks.named(LifecycleBasePlugin.ASSEMBLE_TASK_NAME).configure { Task assemble ->
        assemble.dependsOn rootTaskProvider
      }
    }

    LogoGenBasePlugin.GENERATORS.each { String id, Class<LogoGenerator> logoGeneratorClass ->
      TaskProvider<LogoGenerator> logoGeneratorProvider = project.tasks.register(id, logoGeneratorClass) { LogoGenerator logoGenerator ->
        logoGenerator.group = LifecycleBasePlugin.BUILD_GROUP
        logoGenerator.srcFile.set extension.srcFile
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
        2. ArtifactoryPublish & plugications
       */
      // TODO: webclips ??
    }
  }
}
