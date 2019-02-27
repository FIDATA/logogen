package org.fidata.logogen.shared

import groovy.transform.CompileStatic
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

@CompileStatic
interface LogoName {
  @Input
  Property<String> getLogoName()
}
