// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import groovy.transform.CompileStatic
import org.fidata.logogen.LogoGeneratorDescriptor
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

/**
 * Twitter Profile Photo
 *
 * File format: JPG/GIF/PNG
 * Size: 400
 *
 * References:
 */
@CompileStatic
final class Twitter extends SimpleConverter {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('twitter', Twitter, 'twitter', null)

  @Inject
  Twitter(WorkerExecutor workerExecutor) {
    super(workerExecutor, 400, 'png')
  }
}
