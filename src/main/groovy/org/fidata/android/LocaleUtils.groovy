package org.fidata.android

import com.ibm.icu.util.ULocale
import groovy.transform.CompileStatic

@CompileStatic
final class LocaleUtils {
  static List<String> getRtlLocales() {
    ULocale.availableLocales.findAll { ULocale uLocale ->
      uLocale.name.length() == 2 && uLocale.rightToLeft
    }*.name
  }

  private LocaleUtils() {
    throw new UnsupportedOperationException()
  }
}
