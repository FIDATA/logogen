// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import groovy.transform.CompileStatic
import groovy.transform.Immutable

@Immutable
@CompileStatic
class FreeDesktopConfiguration implements Serializable {
  final String theme

  final Set<Integer> sizes
}
