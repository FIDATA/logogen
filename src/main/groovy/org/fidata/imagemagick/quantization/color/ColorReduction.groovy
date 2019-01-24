package org.fidata.imagemagick.quantization.color

import groovy.transform.CompileStatic
import groovy.transform.KnownImmutable
import org.im4java.core.IMOperation

@KnownImmutable
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
