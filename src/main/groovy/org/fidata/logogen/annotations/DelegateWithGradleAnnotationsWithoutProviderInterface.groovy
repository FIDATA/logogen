package org.fidata.logogen.annotations

import groovy.transform.AnnotationCollector
import org.gradle.api.provider.Provider

// TODO: @DelegateWithoutProviderInterface(methodAnnotations = true)
@Delegate(methodAnnotations = true, interfaces = false, excludeTypes = [Provider])
@AnnotationCollector
@interface DelegateWithGradleAnnotationsWithoutProviderInterface {
}