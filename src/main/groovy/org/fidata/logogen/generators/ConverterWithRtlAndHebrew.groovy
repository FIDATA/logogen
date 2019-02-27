#!/usr/bin/env groovy
/*
 * ConverterWithRtl Gradle task class
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
package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import org.fidata.logogen.shared.HebrewLogoGenerationMethod
import org.fidata.logogen.shared.RtlLogoGenerationMethod
import org.fidata.logogen.shared.HebrewLogoConfigurationProvider
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

/**
 * Logo Generator that is able to generate a separate icons for RTL and Hebrew locales
 */
@CompileStatic
abstract class ConverterWithRtlAndHebrew extends ConverterWithRtl {
  @Delegate(methodAnnotations = true)
  private final HebrewLogoConfigurationProvider hebrewLogoConfigurationProvider
  {
    hebrewLogoConfigurationProvider = new HebrewLogoConfigurationProvider(project.objects)
    hebrewLogoGenerationMethod.convention project.providers.provider {
      hebrewSrcFile.present ? HebrewLogoGenerationMethod.SEPARATE_SOURCE : HebrewLogoGenerationMethod.STANDARD_RTL
    }
  }

  /**
   * ImageMagick {@code convert} operation
   *
   * <b>Constructor of class inheriting this one should have
   * {@code srcFile}, {@code debug}, {@code outputDir}, {@code rtlSrcFile}, {@code rtlLogoGenerationMethod},
   * {@code hebrewSrcFile} and {@code hebrewLogoGenerationMethod}
   * as first seven arguments and pass them to {@code super} constructor.
   * All other arguments may be placed after these seven.</b>
   */
  abstract protected static class ImageMagickConvertOperation extends ConverterWithRtl.ImageMagickConvertOperation {
    private File hebrewSrcFile
    private HebrewLogoGenerationMethod hebrewLogoGenerationMethod

    @Inject
    ImageMagickConvertOperation(File srcFile, boolean debug = false, File outputDir, File rtlSrcFile = null, RtlLogoGenerationMethod rtlIconGenerationMethod, File hebrewSrcFile = null, HebrewLogoGenerationMethod hebrewLogoGenerationMethod) {
      super(srcFile, debug, outputDir, rtlSrcFile, rtlIconGenerationMethod)
      this.@hebrewSrcFile = hebrewSrcFile
      this.@hebrewLogoGenerationMethod = hebrewLogoGenerationMethod
    }
  }

  /**
   * @inheritDocs
   */
  @Override
  protected void imageMagicConvert(WorkerExecutor workerExecutor, Class<? extends ImageMagickConvertOperation> actionClass, Object... params) {
    Object[] newParams = new Object[params.length + 2]
    newParams[0] = hebrewSrcFile.getOrNull()
    newParams[1] = hebrewLogoGenerationMethod.get()
    System.arraycopy(params, 0, newParams, 2, params.length)
    super.imageMagicConvert(workerExecutor, actionClass, newParams)
  }
}
