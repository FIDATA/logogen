// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.shared.properties

import groovy.transform.CompileStatic
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile

@CompileStatic
interface ConfigurableDefault {
  /**
   * Source file, in SVG format
   */
  @InputFile
  RegularFileProperty getSrcFile()
}
