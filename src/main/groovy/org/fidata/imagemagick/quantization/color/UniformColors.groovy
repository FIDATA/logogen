package org.fidata.imagemagick.quantization.color

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.gradle.api.tasks.Input
import org.im4java.core.IMOperation

@Immutable
@CompileStatic
final class UniformColors extends ColorReduction {
  @Input
  final Integer levels

  @Override
  IMOperation toIMOperation() {
    IMOperation operation = new IMOperation()
    operation.p_dither().posterize(levels)
    operation
  }
}
