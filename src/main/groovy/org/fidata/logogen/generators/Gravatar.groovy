#!/usr/bin/env groovy
/*
 * Gravatar Image Generator
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
package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import org.fidata.logogen.LogoGeneratorDescriptor
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

/**
 * Gravatar Image
 *
 * File format: JPG/GIF/PNG
 * Size: 2048
 *
 * References:
 * 1. Image Requests
 *    https://ru.gravatar.com/site/implement/images/
 */
@CompileStatic
final class Gravatar extends SimpleConverter {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('gravatar', Gravatar, 'gravatar', null)

  @Inject
  Gravatar(WorkerExecutor workerExecutor) {
    super(workerExecutor, 2048, 'png')
  }
}
