package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import org.fidata.imagemagick.Compress
import org.fidata.imagemagick.quantization.color.ColorReduction
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.util.ConfigureUtil

@CompileStatic
class WindowsMainIconConfigurationProviderImpl {
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

  @Delegate
  private final Provider<WindowsMainIconConfiguration> provider

  private final ProviderFactory providerFactory
  private final ObjectFactory objectFactory

  WindowsMainIconConfigurationProviderImpl(ProviderFactory providerFactory, ObjectFactory objectFactory) {
    this.@providerFactory = providerFactory
    this.@objectFactory = objectFactory

    this.@depths = objectFactory.mapProperty(Integer, ColorDepth)
    this.@sizes = objectFactory.setProperty(Integer).empty()
    this.@compress = objectFactory.property(Compress)

    this.@provider = providerFactory.provider {
      new WindowsMainIconConfiguration(
        /*(Map<Integer, WindowsMainIconConfiguration.ColorDepth>)*/depths.get().collectEntries { Integer depth, WindowsMainIconConfigurationProviderImpl.ColorDepth colorDepth ->
          [(depth): new WindowsMainIconConfiguration.ColorDepth(colorDepth.sizes.get(), colorDepth.reduction.getOrNull())]
        },
        compress.get(),
      )
    }
  }

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
