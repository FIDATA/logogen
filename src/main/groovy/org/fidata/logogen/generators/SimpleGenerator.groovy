// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators

import com.google.common.collect.ImmutableSet
import groovy.transform.CompileStatic
import java.nio.file.Path
import java.nio.file.Paths
import org.fidata.groovy.utils.InheritInjectableConstructors
import org.fidata.imagemagick.Units
import org.fidata.logogen.AbstractGenerator
import org.gradle.api.provider.Provider
import org.gradle.workers.WorkerExecutor
import org.im4java.core.IMOperation

@CompileStatic
abstract class SimpleGenerator extends AbstractGenerator {
  abstract static class SimpleGeneratorTask extends AbstractGenerator.AbstractGeneratorTask<SimpleGeneratorConfiguration, Provider<SimpleGeneratorConfigurationForIM>> {
    @Delegate(includeTypes = [ConfigurableSimpleGeneratorConfiguration], interfaces = false, methodAnnotations = true)
    protected final SimpleGeneratorConfigurationPropertyImpl simpleGeneratorConfiguration

    @Override
    protected Provider<SimpleGeneratorConfigurationForIM> getConfiguration() {
      this.@simpleGeneratorConfiguration
    }

    @Override
    protected Set<Class<? extends AbstractGenerator.AbstractGeneratorTask.IMConvertOperation<? extends SimpleGeneratorConfiguration>>> getIMConvertOperationClasses() {
      ImmutableSet.of(IMConvertOperation)
    }

    @Override
    protected final Class<DefaultOutputLayout> getDefaultOutputLayoutClass() {
      DefaultOutputLayout
    }

    SimpleGeneratorTask(WorkerExecutor workerExecutor, int size, String format, Integer density = null) {
      super(workerExecutor)
      this.@simpleGeneratorConfiguration = new SimpleGeneratorConfigurationPropertyImpl(project.providers, project.objects, project.layout, size, format, density)
      logoName.convention project.providers.provider {
        project.group.toString()
      }
    }

    @InheritInjectableConstructors
    private final static class IMConvertOperation extends AbstractGenerator.AbstractGeneratorTask.IMConvertOperation<SimpleGeneratorConfigurationForIM> {
      // TODO
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

    private final static class DefaultOutputLayout implements AbstractGenerator.AbstractGeneratorTask.OutputLayout<SimpleGeneratorConfigurationForIM> {
      @Override
      Path getOutputPath(SimpleGeneratorConfigurationForIM configuration, Map<String, ?> context) {
        Paths.get("${ configuration.logoName }.$configuration.format")
      }
    }
  }
}
