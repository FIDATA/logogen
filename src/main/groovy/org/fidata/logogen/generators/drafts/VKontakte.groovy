// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators.drafts

import groovy.transform.CompileStatic
import org.fidata.logogen.LogoGeneratorDescriptor
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

/**
 * VKontakte Profile Photo
 *
 * File format: JPG/GIF/PNG
 * Size: 200 (width) x 500 (height)
 *
 * References:
 * 1. https://vk.com/topic-68859379_30163629
 * 2. https://vk.com/page-11770709_44565679
 */
@CompileStatic
final class VKontakte extends SimpleConverter {
  public static final LogoGeneratorDescriptor DESCRIPTOR = new LogoGeneratorDescriptor('vkontakte', VKontakte, 'vkontakte', null)

  @Inject
  VKontakte(WorkerExecutor workerExecutor) {
    super(workerExecutor, 200, 'png')
  }
}
