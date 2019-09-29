// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import groovy.transform.CompileStatic
import org.fidata.logogen.LogoGeneratorDescriptor
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

/**
 * GitHub Organization Profile Picture
 *
 * File format: PNG/JPG/GIF
 * Size: 500
 *
 * References:
 * 1. How do I set up my profile picture?
 *    https://help.github.com/articles/how-do-i-set-up-my-profile-picture/
 */
@CompileStatic
final class GitHub extends SimpleConverter {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('github', GitHub, 'github', null)

  @Inject
  GitHub(WorkerExecutor workerExecutor) {
    super(workerExecutor, 500, 'png')
  }
}
