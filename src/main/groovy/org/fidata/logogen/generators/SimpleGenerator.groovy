// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators

import com.google.common.collect.ImmutableSet
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import org.fidata.imagemagick.Units
import org.fidata.logogen.AbstractGenerator
import org.gradle.api.provider.Provider
import org.gradle.workers.WorkerExecutor
import org.im4java.core.IMOperation

@CompileStatic
abstract class SimpleGenerator extends AbstractGenerator {
  abstract class SimpleGeneratorTask extends AbstractGenerator.AbstractGeneratorTask<SimpleGeneratorConfiguration, Provider<SimpleGeneratorConfigurationForIM>> {
    @Delegate(includeTypes = [ConfigurableSimpleGeneratorConfiguration])
    protected final SimpleGeneratorConfigurationPropertyImpl simpleGeneratorConfiguration

    @Override
    protected Provider<SimpleGeneratorConfigurationForIM> getConfiguration() {
      this.@simpleGeneratorConfiguration
    }

    @Override
    protected Set<Class<? extends ImageMagickConvertOperation<? extends SimpleGeneratorConfiguration>>> getImageMagicConvertOperationClasses() {
      ImmutableSet.of(ImageMagickResizeAndConvertOperation)
    }

    SimpleGeneratorTask(WorkerExecutor workerExecutor, int size, String format, Integer density = null) {
      super(workerExecutor)
      this.@simpleGeneratorConfiguration = new SimpleGeneratorConfigurationPropertyImpl(project.providers, project.objects, project.layout, size, format, density)
      logoName.convention project.providers.provider {
        project.group.toString()
      }
    }

    @InheritConstructors
    final protected static class ImageMagickResizeAndConvertOperation extends AbstractGenerator.AbstractGeneratorTask.ImageMagickConvertOperation<SimpleGeneratorConfigurationForIM> {
      @Override
      protected IMOperation getOperation() {
        IMOperation operation = new IMOperation()
        operation.background('none')
        if (configuration.density != null) {
          operation
            .units(Units.PIXELSPERINCH.toString())
            .density(configuration.density)
        }
        operation
          .resize(configuration.size, configuration.size)
        operation
      }
    }

    @Override
    protected final Class<OutputLayout> getOutputLayoutClass() {
      OutputLayout
    }

    final class OutputLayout extends AbstractGenerator.AbstractGeneratorTask.OutputLayout<SimpleGeneratorConfigurationForIM> {
      @Override
      File getOutputDirFileName(SimpleGeneratorConfigurationForIM configuration, Map<String, ?> context) {
        new File("${ configuration.logoName }.$configuration.format")
      }
    }
  }
}
