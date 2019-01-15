#!/usr/bin/env groovy
/*
 * VK Profile Photo Generator
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

/**
 * VK Profile Photo
 *
 * File format: JPG/GIF/PNG
 * Size: 200 (width) x 500 (height)
 *
 * References:
 * 1. https://vk.com/vkrazmer
 * 2. https://vk.com/page-11770709_44565679
 */
@CompileStatic
final class VK extends LogoResizeAndConvertGenerator {
  VK() {
    super(200, 'png')
  }

  @Override
  String getGeneratorName() {
    'vk'
  }
}
