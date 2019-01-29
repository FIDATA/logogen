package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import org.fidata.imagemagick.quantization.color.ColorReduction
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.util.ConfigureUtil

@CompileStatic
final class WindowsMainIconExtension {
  private final ProviderFactory providerFactory
  private final ObjectFactory objectFactory

  final MapProperty<Integer, ColorDepth> depths = objectFactory.mapProperty(Integer, ColorDepth)

  final ListProperty<Integer> sizes = objectFactory.listProperty(Integer).empty().convention(
    [256, 96, 72, 64, 60, 48, 40, 32, 24, 20, 16]
  )

  WindowsMainIconExtension(ProviderFactory providerFactory, ObjectFactory objectFactory) {
    this.@providerFactory = providerFactory
    this.@objectFactory = objectFactory
  }

  void depth(int depth, @DelegatesTo(ColorDepth) Closure configureClosure) {
    depths.put depth, providerFactory.provider {
      ColorDepth colorDepth = new ColorDepth()
      ConfigureUtil.configure configureClosure, colorDepth
    }
  }

  final class ColorDepth {
    final ListProperty<Integer> sizes

    public final Property<ColorReduction> reduction

    ColorDepth() {
      this.@sizes = objectFactory.listProperty(Integer).convention(WindowsMainIconExtension.this.sizes)
      this.@reduction = objectFactory.property(ColorReduction)
    }
  }
}
