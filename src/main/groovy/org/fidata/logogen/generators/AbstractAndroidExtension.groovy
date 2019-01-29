package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import groovy.transform.KnownImmutable
import org.fidata.android.DensityFactor
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.SetProperty

@KnownImmutable
@CompileStatic
abstract class AbstractAndroidExtension {
  final SetProperty<DensityFactor> densityFactors

  AbstractAndroidExtension(ObjectFactory objectFactory) {
    this.@densityFactors = objectFactory.setProperty(DensityFactor)
  }
}
