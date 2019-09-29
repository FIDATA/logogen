// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import groovy.transform.CompileStatic
import org.fidata.logogen.LogoGeneratorDescriptor
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

/**
 * GooglePlus Community Photo
 *
 * File format: JPG/GIF/PNG ?
 * Size: 250
 *
 * References:
 */
@CompileStatic
final class GooglePlus extends SimpleConverter {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('googlePlus', GooglePlus, 'googlePlus', null)

  @Inject
  GooglePlus(WorkerExecutor workerExecutor) {
    super(workerExecutor, 250, 'png')
  }
}
