package org.fidata.logogen

import groovy.transform.CompileStatic
import org.fidata.logogen.generators.LogoGenerator

// TODO: @KnownImmutable
@CompileStatic
final class LogoGeneratorDescriptor {
  final String name
  final Class<? extends LogoGenerator> generatorClass

  LogoGeneratorDescriptor(String name, Class<? extends LogoGenerator> generatorClass) {
    this.@name = name
    this.@generatorClass = generatorClass
  }
}
