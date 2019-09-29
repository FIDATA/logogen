// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.fidata.imagemagick.Compress
import org.fidata.imagemagick.quantization.color.ColorReduction

@Immutable
@CompileStatic
final class WindowsMainIconConfiguration implements Serializable {
  final Map<Integer, ColorDepth> depths

  @Immutable
  final static class ColorDepth implements Serializable {
    final Set<Integer> sizes
    final ColorReduction colorReduction
  }

  final Compress compress
}
