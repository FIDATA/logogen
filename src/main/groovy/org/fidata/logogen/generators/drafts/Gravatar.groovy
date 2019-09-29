// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import groovy.transform.CompileStatic
import org.fidata.logogen.LogoGeneratorDescriptor
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

/**
 * Gravatar Image
 *
 * File format: JPG/GIF/PNG
 * Size: 2048
 *
 * References:
 * 1. Image Requests
 *    https://ru.gravatar.com/site/implement/images/
 */
@CompileStatic
final class Gravatar extends SimpleConverter {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('gravatar', Gravatar, 'gravatar', null)

  @Inject
  Gravatar(WorkerExecutor workerExecutor) {
    super(workerExecutor, 2048, 'png')
  }
}
