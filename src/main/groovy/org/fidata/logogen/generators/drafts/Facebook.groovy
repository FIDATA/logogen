// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import groovy.transform.CompileStatic
import org.fidata.logogen.LogoGeneratorDescriptor
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

/**
 * Facebook Profile Picture
 *
 * File format: JPG/GIF/PNG
 * Size: 180
 *
 * References:
 */
@CompileStatic
final class Facebook extends SimpleConverter {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('facebook', Facebook, 'facebook', null)

  @Inject
  Facebook(WorkerExecutor workerExecutor) {
    super(workerExecutor, 180, 'png')
  }
}
