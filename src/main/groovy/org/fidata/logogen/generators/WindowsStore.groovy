#!/usr/bin/env groovy
/*
 * Windows Store App Tile Icon Generator
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
package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import org.fidata.logogen.LogoGeneratorDescriptor
import org.gradle.workers.WorkerExecutor

import javax.inject.Inject

/**
 * Windows Store App Tile Icon
 *
 * File format: PNG
 * Size: 300
 * Default density: 96
 *
 * References:
 * 1. App tile icon // Create app descriptions
 *    https://msdn.microsoft.com/en-US/library/windows/apps/mt148529.aspx#icon
 */
@CompileStatic
final class WindowsStore extends SimpleConverter {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('windowsStore', WindowsStore, 'windowsStore', null)

  @Inject
  WindowsStore(WorkerExecutor workerExecutor) {
    super(workerExecutor, 300, 'png', 96)
  }
}
