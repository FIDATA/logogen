package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import org.gradle.api.tasks.TaskAction

@CompileStatic
abstract class LogoResizeAndConvertGenerator extends LogoGenerator {
  private final int size
  private final String format
  private final Integer density

  LogoResizeAndConvertGenerator(int size, String format, Integer density = null) {
    this.@size = size
    this.@format = format
    this.@density = density
  }

  @TaskAction
  protected final void resizeAndConvert() {
    exec [imconv,
      ] + (debug ? ['-verbose'] : []) + [
        '-background', 'none',
      ] + (density != null ? [
        '-units', 'pixelsperinch',
        '-density', density.toString(),
      ] : []) +
      [
        srcFile,
        '-resize', "${ size }x${ size }",
        file("$outputDir/${project.group}.$format").toString()
      ]
  }
}
