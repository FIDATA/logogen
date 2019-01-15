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
import org.fidata.logogen.generators.*
import org.gradle.api.Plugin
import org.gradle.api.Project

@CompileStatic
final class LogoGenBasePlugin implements Plugin<Project> {
  public static final Map<String, Class<LogoGenerator>> GENERATORS = ImmutableMap.copyOf([
    android: Android,
    appStore: AppStore,
    facebook: Facebook,
    favicon: Favicon,
    freeDesktop: FreeDesktop,
    github: GitHub,
    googlePlay: GooglePlay,
    googlePlus: GooglePlus,
    gravatar: Gravatar,
    iOS: IOS,
    iOS6: IOS6,
    launchPad: LaunchPad,
    odnoklassniki: Odnoklassniki,
    openHub: OpenHub,
    osx: OSX,
    tritter: Twitter,
    vk: VK,
    windowsMainIcon: WindowsMainIcon,
    windowsPhone: WindowsPhone,
    windowsStore: WindowsStore,
    windowsTiles: WindowsTiles,
    windowsTilesForDestopApp: WindowsTilesForDesktopApp,
  ])

  @Override
  void apply(Project project) {
    GENERATORS.values().each { Class<LogoGenerator> logoGeneratorClass ->
      project.extensions.extraProperties[logoGeneratorClass.simpleName] = logoGeneratorClass
    }
  }
}
