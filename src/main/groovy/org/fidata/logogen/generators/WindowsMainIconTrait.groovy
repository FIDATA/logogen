package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import org.fidata.imagemagick.Compress
import org.fidata.imagemagick.quantization.color.ColorReduction
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.util.ConfigureUtil

@CompileStatic
trait WindowsMainIconTrait {
  @Nested
  final MapProperty<Integer, ColorDepth> depths

  void depth(int depth, @DelegatesTo(ColorDepth) Closure configureClosure) {
    depths.put depth, providerFactory.provider {
      ColorDepth colorDepth = new ColorDepth(objectFactory, sizes)
      ConfigureUtil.configure configureClosure, colorDepth
    }
  }

  @Internal
  final SetProperty<Integer> sizes

  @Input
  final Property<Compress> compress

  protected final ProviderFactory providerFactory
  protected final ObjectFactory objectFactory

  final static class ColorDepth {
    @Input
    final SetProperty<Integer> sizes

    @Input
    final Property<ColorReduction> reduction

    ColorDepth(ObjectFactory objectFactory, SetProperty<Integer> defaultSizes) {
      this.@sizes = objectFactory.setProperty(Integer).empty().convention(defaultSizes)
      this.@reduction = objectFactory.property(ColorReduction)
    }
  }
}