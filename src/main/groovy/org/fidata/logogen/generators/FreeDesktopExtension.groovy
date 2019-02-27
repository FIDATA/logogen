package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import org.fidata.logogen.annotations.DelegateWithoutProviderInterface
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

@CompileStatic
final class FreeDesktopExtension {
  @DelegateWithoutProviderInterface
  private final FreeDesktopConfigurationProviderImpl freeDesktopConfigurationProvider

  @Inject
  FreeDesktopExtension(ProviderFactory providerFactory, ObjectFactory objectFactory) {
    this.@freeDesktopConfigurationProvider = new FreeDesktopConfigurationProviderImpl(providerFactory, objectFactory)
    theme.convention(providerFactory.provider { 'hicolor' })
    sizes.convention(providerFactory.provider {
      [16, 22, 24, 32, 36, 48, 64, 72, 96, 128, 192, 256, 512]
    })
  }
}
