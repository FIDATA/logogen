// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.imagemagick.quantization.color

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.fidata.imagemagick.IMOperationWithoutIO
import org.gradle.api.tasks.Input
import org.im4java.core.IMOperation

@Immutable
@CompileStatic
final class UniformColors extends ColorReduction {
  @Input
  final Integer levels

  @Override
  IMOperationWithoutIO toIMOperation() {
    IMOperationWithoutIO operation = new IMOperationWithoutIO()
    operation.p_dither().posterize(levels)
    operation
  }
}
