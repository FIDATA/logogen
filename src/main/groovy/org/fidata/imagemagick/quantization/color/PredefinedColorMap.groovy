// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.imagemagick.quantization.color

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.fidata.imagemagick.IMOperationWithoutIO
import org.gradle.api.tasks.InputFile
import org.im4java.core.IMOperation

@Immutable(knownImmutableClasses = [File])
@CompileStatic
final class PredefinedColorMap extends ColorReduction {
  @InputFile
  final File colorMap

  @Override
  IMOperationWithoutIO toIMOperation() {
    IMOperationWithoutIO operation = new IMOperationWithoutIO()
    operation.p_dither().remap(colorMap.toString())
    operation
  }
}
