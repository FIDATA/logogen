// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.imagemagick

import groovy.transform.CompileStatic

@CompileStatic
enum Units {
  UNDEFINED('Undefined'),
  PIXELSPERINCH('PixelsPerInch'),
  PIXELSPERCENTIMETER('PixelsPerCentimeter')

  final String name

  Units(String name) {
    this.@name = name
  }

  @Override
  String toString() {
    name
  }
}
