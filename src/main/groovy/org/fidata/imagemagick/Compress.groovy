// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.imagemagick

import groovy.transform.CompileStatic

@CompileStatic
enum Compress {
  NONE('None'),
  BZIP('BZip'),
  FAX('Fax'),
  GROUP4('Group4'),
  JPEG('JPEG'),
  JPEG2000('JPEG2000'),
  LOSSLESS('Lossless'),
  LZW('LZW'),
  RLE('RLE'),
  /**
   * When writing an ICO file, you may request that the images be encoded in PNG format, by specifying Zip compression.
   */
  ZIP('Zip')

  final String name

  Compress(String name) {
    this.@name = name
  }

  @Override
  String toString() {
    name
  }
}
