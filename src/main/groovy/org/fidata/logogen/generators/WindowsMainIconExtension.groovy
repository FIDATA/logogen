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
final class WindowsMainIconExtension {
  private final ProviderFactory providerFactory
  private final ObjectFactory objectFactory

  final MapProperty<Integer, ColorDepth> depths = objectFactory.mapProperty(Integer, ColorDepth).convention(providerFactory.provider {
    Map<Integer, ColorDepth> result = [
      4: new ColorDepth(objectFactory, sizes),
      8: new ColorDepth(objectFactory, sizes),
      (Integer)null: new ColorDepth(objectFactory, sizes)
    ]
    result[4].reduction.set new UniformColors(3)
    result
  })

  void depth(int depth, @DelegatesTo(ColorDepth) Closure configureClosure) {
    depths.put depth, providerFactory.provider {
      ColorDepth colorDepth = new ColorDepth(objectFactory, sizes)
      ConfigureUtil.configure configureClosure, colorDepth
    }
  }

  final SetProperty<Integer> sizes = objectFactory.setProperty(Integer).empty().convention(
    [256, 96, 72, 64, 60, 48, 40, 32, 24, 20, 16]
  )

  final Property<Compress> compress = objectFactory.property(Compress).convention(Compress.NONE)

  @Inject
  WindowsMainIconExtension(ProviderFactory providerFactory, ObjectFactory objectFactory) {
    this.@providerFactory = providerFactory
    this.@objectFactory = objectFactory
  }

  final static class ColorDepth {
    final SetProperty<Integer> sizes

    final Property<ColorReduction> reduction

    ColorDepth(ObjectFactory objectFactory, SetProperty<Integer> defaultSizes) {
      this.@sizes = objectFactory.setProperty(Integer).empty().convention(defaultSizes)
      this.@reduction = objectFactory.property(ColorReduction)
    }
  }
}
