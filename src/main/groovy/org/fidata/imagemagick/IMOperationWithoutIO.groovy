package org.fidata.imagemagick

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.im4java.core.IMOperation
import org.im4java.core.Operation

@Immutable(knownImmutableClasses = [IMOperation])
@CompileStatic
final class IMOperationWithoutIO {
  private final IMOperation imOperation = new IMOperation()

  IMOperationWithoutIO background(String var1) {
    imOperation.units(Units.PIXELSPERINCH.toString())
    imOperation.background(var1)
    this
  }

  IMOperationWithoutIO density(Integer var1) {
    imOperation.density(var1)
    this
  }

  IMOperationWithoutIO compress(String var1) {
    imOperation.compress(var1)
    this
  }

  IMOperationWithoutIO colors(Integer var1) {
    imOperation.colors(var1)
    this
  }

  IMOperationWithoutIO depth(Integer var1) {
    imOperation.depth(var1)
    this
  }

  IMOperationWithoutIO resize(Integer var1, Integer var2) {
    imOperation.resize(var1, var2)
    this
  }

  @SuppressWarnings('GroovyInstanceMethodNamingConvention')
  IMOperationWithoutIO p_dither() {
    imOperation.p_dither()
    this
  }

  IMOperationWithoutIO remap(String var1) {
    imOperation.remap(var1)
    this
  }

  IMOperationWithoutIO separate() {
    imOperation.separate()
    this
  }

  IMOperationWithoutIO threshold(Integer var1, Integer var2, Integer var3, Integer var4, Boolean var5) {
    imOperation.threshold(var1, var2, var3, var4, var5)
    this
  }

  IMOperationWithoutIO posterize(Integer var1) {
    imOperation.posterize(var1)
    this
  }

  IMOperationWithoutIO combine() {
    imOperation.combine()
    this
  }
  // TOTHINK
  IMOperationWithoutIO openOperation() {
    imOperation.openOperation()
    this
  }

  // TOTHINK
  IMOperationWithoutIO closeOperation() {
    imOperation.closeOperation()
    this
  }

  IMOperationWithoutIO addSubOperation(IMOperationWithoutIO var1) {
    imOperation.addSubOperation(var1.toIMOperation())
    this
  }

  Operation toIMOperation() {
    imOperation.cloneObject()
  }
}
