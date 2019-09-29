// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.imagemagick.quantization.color

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.gradle.api.tasks.InputFile
import org.im4java.core.IMOperation

@Immutable(knownImmutableClasses = [File])
@CompileStatic
final class PredefinedColorMap extends ColorReduction {
  @InputFile
  final File colorMap

  @Override
  IMOperation toIMOperation() {
    IMOperation operation = new IMOperation()
    operation.p_dither().remap(colorMap.toString())
    operation
  }
}
