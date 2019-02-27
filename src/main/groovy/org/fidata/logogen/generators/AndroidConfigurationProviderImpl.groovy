package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import org.fidata.android.DensityFactor
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input

@CompileStatic
class AndroidConfigurationProviderImpl {
  @Input
  final SetProperty<DensityFactor> densityFactors

  @Delegate
  private final Provider<AndroidConfiguration> provider

  AndroidConfigurationProviderImpl(ProviderFactory providerFactory, ObjectFactory objectFactory) {
    this.@densityFactors = objectFactory.setProperty(DensityFactor)

    this.@provider = providerFactory.provider {
      new AndroidConfiguration(
        densityFactors.get(),
      )
    }
  }
}