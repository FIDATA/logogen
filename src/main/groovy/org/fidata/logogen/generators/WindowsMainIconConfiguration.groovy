package org.fidata.logogen.generators

import groovy.transform.Immutable
import org.fidata.imagemagick.Compress
import org.fidata.imagemagick.quantization.color.ColorReduction

@Immutable
final class WindowsMainIconConfiguration {
  final List<ColorDepth> depths

  @Immutable
  final static class ColorDepth {
    final List<Integer> sizes
    final ColorReduction colorReduction
  }

  final Compress compress
}
