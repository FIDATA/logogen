#!/usr/bin/env groovy
/*
 * LogoGeneratorDescriptor class
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

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.fidata.logogen.generators.LogoGenerator

/**
 * Descriptor of Logo Generator
 */
@Immutable
@CompileStatic
final class LogoGeneratorDescriptor {
  /**
   * Name of generator. It's used as names of a task and output directory.
   * It is recommended to use {@code camelCase} for the name
   */
  final String name
  /**
   * Class implementing generator
   */
  final Class<? extends LogoGenerator> implementationClass
  /**
   * Extension with configuration of generator
   */
  final Class<?> extensionClass
}
