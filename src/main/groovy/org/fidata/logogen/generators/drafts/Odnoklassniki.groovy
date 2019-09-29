// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import groovy.transform.CompileStatic
import org.fidata.logogen.LogoGeneratorDescriptor
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

/**
 * Odnoklassniki Profile Photo
 *
 * File format: JPG/GIF/PNG
 * Size: 190
 *
 * References:
 */
@CompileStatic
final class Odnoklassniki extends SimpleConverter {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('odnoklassniki', Odnoklassniki, 'odnoklassniki', null)

  @Inject
  Odnoklassniki(WorkerExecutor workerExecutor) {
    super(workerExecutor, 190, 'png')
  }
}
