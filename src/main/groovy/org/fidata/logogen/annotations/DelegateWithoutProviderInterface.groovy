package org.fidata.logogen.annotations

import groovy.transform.AnnotationCollector
import org.gradle.api.provider.Provider

@Delegate(interfaces = false, excludeTypes = [Provider])
@AnnotationCollector
@interface DelegateWithoutProviderInterface {
}
