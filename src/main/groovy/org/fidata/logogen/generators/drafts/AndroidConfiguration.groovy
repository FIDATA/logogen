// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.fidata.android.DensityFactor

@Immutable
@CompileStatic
final class AndroidConfiguration implements Serializable {
  final Set<DensityFactor> densityFactors
}
