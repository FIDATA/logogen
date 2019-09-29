// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import static WindowsMainIconConfigurationProviderImpl.ColorDepth
import org.fidata.logogen.annotations.DelegateWithoutProviderInterface
import groovy.transform.CompileStatic
import groovy.transform.KnownImmutable
import org.fidata.imagemagick.Compress
import org.fidata.imagemagick.quantization.color.UniformColors
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

@KnownImmutable
@CompileStatic
final class WindowsMainIconExtension {
  @DelegateWithoutProviderInterface
  private final WindowsMainIconConfigurationProviderImpl windowsMainIconConfigurationProvider

  @Inject
  WindowsMainIconExtension(ProviderFactory providerFactory, ObjectFactory objectFactory) {
    this.@windowsMainIconConfigurationProvider = new WindowsMainIconConfigurationProviderImpl(providerFactory, objectFactory)

    depths.convention(providerFactory.provider {
      Map<Integer, ColorDepth> result = [
        4: new ColorDepth(objectFactory, sizes),
        8: new ColorDepth(objectFactory, sizes),
        (Integer)null: new ColorDepth(objectFactory, sizes)
      ]
      result[4].reduction.set new UniformColors(3)
      result
    })
    sizes.convention(
      [256, 96, 72, 64, 60, 48, 40, 32, 24, 20, 16]
    )
    compress.convention(Compress.NONE)
  }
}
