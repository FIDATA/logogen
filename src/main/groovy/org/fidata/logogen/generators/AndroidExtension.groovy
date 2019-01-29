package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import groovy.transform.KnownImmutable
import org.fidata.android.DensityFactor
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

@KnownImmutable
@CompileStatic
final class AndroidExtension extends AbstractAndroidExtension {
  @Inject
  AndroidExtension(ObjectFactory objectFactory) {
    super(objectFactory)
    densityFactors.convention DensityFactor.values().toList()
  }
}
