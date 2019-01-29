package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import groovy.transform.KnownImmutable
import org.fidata.android.DensityFactor
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

@KnownImmutable
@CompileStatic
final class AndroidPre30Extension extends AbstractAndroidExtension {
  @Inject
  AndroidPre30Extension(ObjectFactory objectFactory) {
    super(objectFactory)
    densityFactors.convention([DensityFactor.LDPI, DensityFactor.MDPI, DensityFactor.HDPI, DensityFactor.XHDPI])
  }
}
