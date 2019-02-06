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
import org.apache.groovy.json.internal.ArrayUtils
import org.fidata.logogen.RtlIconGenerationMethod
import org.gradle.api.Action
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.LogLevel
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.workers.IsolationMode
import org.gradle.workers.WorkerConfiguration
import org.gradle.workers.WorkerExecutor

import javax.inject.Inject

/**
 * Logo Generator that is able to generate a separate icon for RTL locale
 */
@CompileStatic
abstract class LogoGeneratorWithRtl extends LogoGenerator {
  /**
   * Source file for RTL locale
   */
  @InputFile
  @Optional
  final RegularFileProperty rtlSrcFile = project.objects.fileProperty()

  /**
   * Method of creation of RTL icon
   */
  @Input
  final Property<RtlIconGenerationMethod> rtlIconGenerationMethod = project.objects.property(RtlIconGenerationMethod).convention project.providers.provider {
    rtlSrcFile.present ? RtlIconGenerationMethod.SEPARATE_SOURCE : RtlIconGenerationMethod.MIRROW
  }

  /**
   * ImageMagick {@code convert} operation
   *
   * <b>Constructor of class inheriting this one should have
   * {@code srcFile}, {@code debug}, {@code outputDir}, {@code rtlSrcFile} and {@code rtlIconGenerationMethod}
   * as first five arguments and pass them to {@code super} constructor.
   * All other arguments may be placed after these five.</b>
   */
  abstract protected static class ImageMagickConvertOperation extends LogoGenerator.ImageMagickConvertOperation {
    private File rtlSrcFile
    private RtlIconGenerationMethod rtlIconGenerationMethod

    @Inject
    ImageMagickConvertOperation(File srcFile, boolean debug = false, File outputDir, File rtlSrcFile = null, RtlIconGenerationMethod rtlIconGenerationMethod) {
      super(srcFile, debug, outputDir)
      this.@rtlSrcFile = rtlSrcFile
      this.@rtlIconGenerationMethod = rtlIconGenerationMethod
    }
  }

  /**
   * @inheritDocs
   */
  @Override
  protected void imageMagicConvert(WorkerExecutor workerExecutor, Class<? extends ImageMagickConvertOperation> actionClass, Object... params) {
    Object[] newParams = new Object[params.length + 2]
    newParams[0] = rtlSrcFile.getOrNull()
    newParams[1] = rtlIconGenerationMethod.get()
    System.arraycopy(params, 0, newParams, 2, params.length)
    super.imageMagicConvert(workerExecutor, actionClass, newParams)
  }
}
