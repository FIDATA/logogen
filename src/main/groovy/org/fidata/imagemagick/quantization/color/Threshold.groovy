// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.imagemagick.quantization.color

import groovy.transform.CompileStatic
import groovy.transform.Immutable
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
  IMOperation toIMOperation() {
    IMOperation operation = new IMOperation()
    operation.separate().threshold(threshold, null, null, null, true).combine()
    operation
  }
}
