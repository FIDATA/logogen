// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import groovy.transform.CompileStatic
import org.fidata.logogen.LogoGeneratorDescriptor
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

/**
 * GooglePlay High-Res Icon
 *
 * File format: PNG
 * Color depth: 32-bit with alpha
 * Size: 512×512 px
 * Maximum file size: 1024 KB
 *
 * Reference:
 * High-res icon // Graphic assets, screenshots, & video // Play Console Help
 * https://support.google.com/googleplay/android-developer/answer/1078870
 */
@CompileStatic
final class GooglePlay extends SimpleConverter {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('googlePlay', GooglePlay, 'googlePlay', null)

  @Inject
  GooglePlay(WorkerExecutor workerExecutor) {
    super(workerExecutor, 512, 'png')
  }
}
