package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.fidata.android.DensityFactor

@Immutable
@CompileStatic
final class AndroidConfiguration implements Serializable {
  final Set<DensityFactor> densityFactors
}
