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


import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory

import javax.inject.Inject
import java.awt.Color
import org.gradle.api.Project

/**
 * logogen extension for {@link Project}
 */
final class LogoGenExtension {
  /**
   * Logo source, in SVG format
   */
  final RegularFileProperty srcFile

  /**
   * Logo source for RTL locales, in SVG format
   */
  final RegularFileProperty rtlSrcFile

  /**
   * Logo source for Hebrew locale, in SVG format
   */
  final RegularFileProperty hebrewSrcFile

  /**
   * Background color.
   * It is used by generators that require opaque background
   */
  final Property<Color> background

  /**
   * Method of creation of RTL icon.
   * By default it is {@link RtlIconGenerationMethod#SEPARATE_SOURCE}
   * if {@link #rtlSrcFile} is set
   * and {@link RtlIconGenerationMethod#MIRROW} otherwise
   */
  final Property<RtlIconGenerationMethod> rtlIconGenerationMethod

  /**
   * Method of creation of Hebrew icon.
   * By default it is {@link HebrewIconGenerationMethod#SEPARATE_SOURCE}
   * if {@link #hebrewSrcFile} is set
   * and {@link HebrewIconGenerationMethod#STANDARD_RTL} otherwise
   */
  final Property<HebrewIconGenerationMethod> hebrewIconGenerationMethod

  /**
   * Sets background color by name.
   * In current version it is decoded with {@link Color#decode(String)}.
   * This could be changed in the future to support more color names
   *
   * @param background Color name
   */
  void setBackground(String background) {
    this.@background.set Color.decode(background)
  }

  /**
   * Construct new LogoGenExtension object
   *
   * @param providerFactory {@link ProviderFactory} instance
   * @param objectFactory {@link ObjectFactory} instance
   */
  @Inject
  protected LogoGenExtension(ProviderFactory providerFactory, ObjectFactory objectFactory) {
    srcFile = objectFactory.fileProperty()
    rtlSrcFile = objectFactory.fileProperty()
    hebrewSrcFile = objectFactory.fileProperty()
    background = objectFactory.property(Color)
    rtlIconGenerationMethod = objectFactory.property(RtlIconGenerationMethod).convention providerFactory.provider {
      rtlSrcFile.present ? RtlIconGenerationMethod.SEPARATE_SOURCE : RtlIconGenerationMethod.MIRROW
    }
    hebrewIconGenerationMethod = objectFactory.property(HebrewIconGenerationMethod).convention providerFactory.provider {
      hebrewSrcFile.present ? HebrewIconGenerationMethod.SEPARATE_SOURCE : HebrewIconGenerationMethod.STANDARD_RTL
    }
  }
}
