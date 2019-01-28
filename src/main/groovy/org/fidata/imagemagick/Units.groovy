package org.fidata.imagemagick

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
