package org.fidata.groovy.utils

import groovy.transform.AnnotationCollector
import groovy.transform.EqualsAndHashCode
import groovy.transform.ImmutableOptions
import groovy.transform.KnownImmutable
import groovy.transform.PropertyOptions
import groovy.transform.ToString

@AnnotationCollector
@ToString
@EqualsAndHashCode
@ImmutableOptions
@PropertyOptions
@KnownImmutable
@interface ImmutableWithCustomConstructors {
}
