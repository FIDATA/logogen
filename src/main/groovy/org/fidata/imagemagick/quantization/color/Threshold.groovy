package org.fidata.imagemagick.quantization.color

import groovy.transform.CompileStatic
import groovy.transform.KnownImmutable
import org.gradle.api.tasks.Input
import org.im4java.core.IMOperation

@KnownImmutable
@CompileStatic
final class Threshold extends ColorReduction {
  /**
   * In percents
   */
  @Input
  final Integer threshold

  Threshold(Integer threshold) {
    this.@threshold = threshold
  }

  @Override
  IMOperation toIMOperation() {
    IMOperation operation = new IMOperation()
    operation.separate().threshold(threshold, null, null, null, true).combine()
    operation
  }
}
