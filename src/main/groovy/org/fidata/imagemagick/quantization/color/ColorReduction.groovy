// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.imagemagick.quantization.color

import groovy.transform.CompileStatic
import org.fidata.imagemagick.IMOperationWithoutIO
import org.im4java.core.IMOperation

@CompileStatic
abstract class ColorReduction implements Serializable {
  abstract IMOperationWithoutIO toIMOperation()

  final Object asType(Class clazz) {
    if (clazz == IMOperation) {
      toIMOperation().toIMOperation()
    }
    super.asType(clazz)
  }
}
