package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import org.fidata.android.DensityFactor
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input

@CompileStatic
trait AndroidTrait {
  @Input
  final SetProperty<DensityFactor> densityFactors
}