// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import groovy.transform.CompileStatic
import groovy.transform.KnownImmutable
import org.fidata.android.DensityFactor
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory

import javax.inject.Inject

@KnownImmutable
@CompileStatic
final class Android4_3Extension extends AbstractAndroidExtension {
  @Inject
  Android4_3Extension(ProviderFactory providerFactory, ObjectFactory objectFactory) {
    super(providerFactory, objectFactory)
    densityFactors.convention DensityFactor.values().toList()
  }
}
