// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.annotations

import groovy.transform.AnnotationCollector
import org.gradle.api.provider.Provider

// TODO: @DelegateWithoutProviderInterface(methodAnnotations = true)
@Delegate(methodAnnotations = true)
@AnnotationCollector
@interface DelegateWithGradleAnnotations {
}