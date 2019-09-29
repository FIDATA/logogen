// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import groovy.transform.CompileStatic
import org.fidata.logogen.LogoGeneratorDescriptor
import org.gradle.workers.WorkerExecutor

import javax.inject.Inject

/**
 * Windows Store App Tile Icon
 *
 * File format: PNG
 * Size: 300
 * Default density: 96
 *
 * References:
 * 1. App tile icon // Create app descriptions
 *    https://msdn.microsoft.com/en-US/library/windows/apps/mt148529.aspx#icon
 */
@CompileStatic
final class WindowsStore extends SimpleConverter {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('windowsStore', WindowsStore, 'windowsStore', null)

  @Inject
  WindowsStore(WorkerExecutor workerExecutor) {
    super(workerExecutor, 300, 'png', 96)
  }
}
