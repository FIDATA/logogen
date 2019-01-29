package org.fidata.imagemagick

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