// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import groovy.transform.CompileStatic
import org.fidata.logogen.LogoGeneratorDescriptor
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

/**
 * Open Hub Project Logo
 *
 * File format: GIF/JPG/PNG
 * Size: 64
 *
 * References:
 * 1. https://www.openhub.net/p/{project}/logos/new
 */
@CompileStatic
final class OpenHub extends SimpleConverter {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('openhub', OpenHub, 'openhub', null)

  @Inject
  OpenHub(WorkerExecutor workerExecutor) {
    super(workerExecutor, 64, 'png')
  }
}
