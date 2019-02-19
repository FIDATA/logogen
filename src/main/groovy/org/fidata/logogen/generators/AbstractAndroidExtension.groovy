package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import groovy.transform.KnownImmutable
import org.fidata.android.DensityFactor
import org.gradle.api.model.ObjectFactory

@KnownImmutable
@CompileStatic
abstract class AbstractAndroidExtension implements AndroidTrait {
  AbstractAndroidExtension(ObjectFactory objectFactory) {
    this.@org_fidata_logogen_generators_AndroidTrait__densityFactors = objectFactory.setProperty(DensityFactor)
  }
}
