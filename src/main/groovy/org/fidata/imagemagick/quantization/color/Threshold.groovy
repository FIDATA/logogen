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
final class Threshold extends ColorReduction {
  /**
   * In percents
   */
  @Input
  final Integer threshold

  @Override
  IMOperationWithoutIO toIMOperation() {
    IMOperationWithoutIO operation = new IMOperationWithoutIO()
    operation.separate().threshold(threshold, null, null, null, true).combine()
    operation
  }
}
