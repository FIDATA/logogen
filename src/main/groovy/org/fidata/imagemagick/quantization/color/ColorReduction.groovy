// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.imagemagick.quantization.color

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.im4java.core.IMOperation

@Immutable
@CompileStatic
abstract class ColorReduction implements Serializable {
  abstract IMOperation toIMOperation()

  @Override
  Object asType(Class clazz) {
    if (clazz == IMOperation) {
      toIMOperation()
    }
    super.asType(clazz)
  }
}
