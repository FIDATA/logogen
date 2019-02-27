package org.fidata.android

import org.junit.Test

class LocaleUtilsTest {
  @Test
  void testGetRtlLocalesOnCommonLanguages() {
    List<String> rtlLocales = LocaleUtils.rtlLocales
    assert Collections.disjoint(rtlLocales, [
      'en',  // English
      'ru',  // Russian
      'zh',  // Chinese
    ])
    assert rtlLocales.containsAll [
      'ar', // Arabic
      'he', // Hebrew
      'fa', // Farsi
    ]
  }

}
