// Copyright (C) 2012 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.gerrit.plugins.zookeeper;

import com.google.gerrit.extensions.annotations.Export;
import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.sshd.SshCommand;
import com.google.inject.Inject;

import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.kohsuke.args4j.Argument;

import java.io.PrintWriter;

/** SSH command for creating repository nodes in Zookeeper. */
@Export("create")
class CreateCommand extends SshCommand {
  @Argument(usage = "project name")
  private String name;

  static void create(ZkRepositoryManager repoManager, Project.NameKey name,
      PrintWriter stdout) throws Exception {
    try {
      repoManager.openRepository(name);
      stdout.write("Project " + name + " already exists");
    } catch (RepositoryNotFoundException e) {
      repoManager.createRepository(name);
    }
  }

  private final ZkRepositoryManager repoManager;

  @Inject
  CreateCommand(ZkRepositoryManager repoManager) {
    this.repoManager = repoManager;
  }

  @Override
  protected void run() throws UnloggedFailure, Failure, Exception {
    if (name == null) {
      throw new UnloggedFailure(1, "project name is required");
    }
    create(repoManager, new Project.NameKey(name), stdout);
  }
}
