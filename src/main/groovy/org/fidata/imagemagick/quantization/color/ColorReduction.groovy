// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.imagemagick.quantization.color

import groovy.transform.CompileStatic
import org.im4java.core.IMOperation

@CompileStatic
abstract class ColorReduction implements Serializable {
  abstract IMOperation toIMOperation()

  final Object asType(Class clazz) {
    if (clazz == IMOperation) {
      toIMOperation()
    }
    super.asType(clazz)
  }
}
