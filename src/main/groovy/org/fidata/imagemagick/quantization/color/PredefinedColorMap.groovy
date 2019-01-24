package org.fidata.imagemagick.quantization.color

import groovy.transform.CompileStatic
import groovy.transform.KnownImmutable
import org.gradle.api.tasks.InputFile
import org.im4java.core.IMOperation

@KnownImmutable
@CompileStatic
final class PredefinedColorMap extends ColorReduction {
  @InputFile
  final File colorMap

  PredefinedColorMap(File colorMap) {
    this.@colorMap = colorMap
  }

  @Override
  IMOperation toIMOperation() {
    IMOperation operation = new IMOperation()
    operation.p_dither().remap(colorMap.toString())
    operation
  }
}
