package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import groovy.transform.KnownImmutable
import org.fidata.imagemagick.Compress
import org.fidata.imagemagick.quantization.color.ColorReduction
import org.fidata.imagemagick.quantization.color.UniformColors
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.provider.SetProperty
import org.gradle.util.ConfigureUtil
import javax.inject.Inject

@KnownImmutable
@CompileStatic
final class WindowsMainIconExtension implements WindowsMainIconTrait {
  @Inject
  WindowsMainIconExtension(ProviderFactory providerFactory, ObjectFactory objectFactory) {
    this.@org_fidata_logogen_generators_WindowsMainIconTrait__providerFactory = providerFactory
    this.@org_fidata_logogen_generators_WindowsMainIconTrait__objectFactory = objectFactory
    this.@org_fidata_logogen_generators_WindowsMainIconTrait__depths = objectFactory.mapProperty(Integer, ColorDepth).convention(providerFactory.provider {
      Map<Integer, ColorDepth> result = [
        4: new ColorDepth(objectFactory, sizes),
        8: new ColorDepth(objectFactory, sizes),
        (Integer)null: new ColorDepth(objectFactory, sizes)
      ]
      result[4].reduction.set new UniformColors(3)
      result
    })
    this.@org_fidata_logogen_generators_WindowsMainIconTrait__sizes = objectFactory.setProperty(Integer).empty().convention(
      [256, 96, 72, 64, 60, 48, 40, 32, 24, 20, 16]
    )
    this.@org_fidata_logogen_generators_WindowsMainIconTrait__compress = objectFactory.property(Compress).convention(Compress.NONE)
  }
}
