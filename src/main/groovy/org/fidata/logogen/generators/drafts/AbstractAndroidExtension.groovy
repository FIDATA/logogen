// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import groovy.transform.CompileStatic
import groovy.transform.KnownImmutable
import org.fidata.logogen.annotations.DelegateWithoutProviderInterface
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory

@KnownImmutable
@CompileStatic
abstract class AbstractAndroidExtension {
  @DelegateWithoutProviderInterface
  private final AndroidConfigurationProviderImpl androidConfigurationProvider

  AbstractAndroidExtension(ProviderFactory providerFactory, ObjectFactory objectFactory) {
    this.@androidConfigurationProvider = new AndroidConfigurationProviderImpl(providerFactory, objectFactory)
  }
}
