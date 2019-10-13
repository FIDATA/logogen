// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.logogen.generators

import groovy.transform.CompileStatic
import javax.inject.Inject
import org.gradle.workers.WorkerExecutor

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
final class GitHub extends SimpleGenerator {
  String getName() {
    'github'
  }

  @Override
  Class<GitHubTask> getConverterImplClass() {
    GitHubTask
  }

  final static class GitHubTask extends SimpleGenerator.SimpleGeneratorTask {
    @Inject
    GitHubTask(WorkerExecutor workerExecutor) {
      super(workerExecutor, 500, 'png')
    }
  }
}
