package org.fidata.android

enum DensityFactor {
  LDPI('ldpi', 0.75),
  MDPI('mdpi', 1.0),
  TVHDPI('tvdpi', 1.33),  // Added in API 13
  HDPI('hdpi', 1.5),
  XHDPI('xhdpi', 2.0),    // Added in API level 8
  XXHDPI('xxhdpi', 3.0),  // Added in API 16
  XXXHDPI('xxxhdpi', 4.0) // Added in API 18

  final String name
  final BigDecimal factor

  DensityFactor(String name, BigDecimal factor) {
    this.@name = name
    this.@factor = factor
  }
}
