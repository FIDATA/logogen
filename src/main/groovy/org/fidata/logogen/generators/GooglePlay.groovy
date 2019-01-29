#!/usr/bin/env groovy
/*
 * GooglePlay High-Res Icon Generator
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
import org.fidata.logogen.LogoGeneratorDescriptor
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

/**
 * GooglePlay High-Res Icon
 *
 * File format: PNG
 * Color depth: 32-bit with alpha
 * Size: 512×512 px
 * Maximum file size: 1024 KB
 *
 * Reference:
 * High-res icon // Graphic assets, screenshots, & video // Play Console Help
 * https://support.google.com/googleplay/android-developer/answer/1078870
 */
@CompileStatic
final class GooglePlay extends LogoResizeAndConvertGenerator {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('googlePlay', GooglePlay, null)

  @Inject
  GooglePlay(WorkerExecutor workerExecutor) {
    super(workerExecutor, 512, 'png')
  }
}
