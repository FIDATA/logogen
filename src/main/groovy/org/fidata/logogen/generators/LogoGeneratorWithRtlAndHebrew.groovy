#!/usr/bin/env groovy
/*
 * LogoGeneratorWithRtl Gradle task class
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
import org.fidata.logogen.HebrewIconGenerationMethod
import org.fidata.logogen.RtlIconGenerationMethod
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.workers.WorkerExecutor

import javax.inject.Inject

/**
 * Logo Generator that is able to generate a separate icons for RTL and Hebrew locales
 */
@CompileStatic
abstract class LogoGeneratorWithRtlAndHebrew extends LogoGeneratorWithRtl {
  /**
   * Source file for Hebrew locale.
   * Provided as separate since Hebrew has different rules
   * on mirroring characters (specifically, question mark is not mirrored).
   * If not provided then TODO
   */
  @InputFile
  @Optional
  final RegularFileProperty hebrewSrcFile = project.objects.fileProperty()

  /**
   * Method of creation of Hebrew icon
   */
  @Input
  final Property<HebrewIconGenerationMethod> hebrewIconGenerationMethod = project.objects.property(HebrewIconGenerationMethod).convention project.providers.provider {
    hebrewSrcFile.present ? HebrewIconGenerationMethod.SEPARATE_SOURCE : HebrewIconGenerationMethod.STANDARD_RTL
  }

  /**
   * ImageMagick {@code convert} operation
   *
   * <b>Constructor of class inheriting this one should have
   * {@code srcFile}, {@code debug}, {@code outputDir}, {@code rtlSrcFile}, {@code rtlIconGenerationMethod},
   * {@code hebrewSrcFile} and {@code hebrewIconGenerationMethod}
   * as first seven arguments and pass them to {@code super} constructor.
   * All other arguments may be placed after these seven.</b>
   */
  abstract protected static class ImageMagickConvertOperation extends LogoGeneratorWithRtl.ImageMagickConvertOperation {
    private File hebrewSrcFile
    private HebrewIconGenerationMethod hebrewIconGenerationMethod

    @Inject
    ImageMagickConvertOperation(File srcFile, boolean debug = false, File outputDir, File rtlSrcFile = null, RtlIconGenerationMethod rtlIconGenerationMethod, File hebrewSrcFile = null, HebrewIconGenerationMethod hebrewIconGenerationMethod) {
      super(srcFile, debug, outputDir, rtlSrcFile, rtlIconGenerationMethod)
      this.@hebrewSrcFile = hebrewSrcFile
      this.@hebrewIconGenerationMethod = hebrewIconGenerationMethod
    }
  }

  /**
   * @inheritDocs
   */
  @Override
  protected void imageMagicConvert(WorkerExecutor workerExecutor, Class<? extends ImageMagickConvertOperation> actionClass, Object... params) {
    Object[] newParams = new Object[params.length + 2]
    newParams[0] = hebrewSrcFile.getOrNull()
    newParams[1] = hebrewIconGenerationMethod.get()
    System.arraycopy(params, 0, newParams, 2, params.length)
    super.imageMagicConvert(workerExecutor, actionClass, newParams)
  }
}
