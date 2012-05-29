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

import static com.google.inject.Scopes.SINGLETON;

import com.google.gerrit.lifecycle.LifecycleModule;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.gerrit.server.git.GitRepositoryManager;
import com.google.inject.Inject;

import org.eclipse.jgit.lib.Config;

/** Registers the Zookeeper-based {@link GitRepositoryManager}. */
public class ZookeeperModule extends LifecycleModule {
  private final Config config;

  @Inject
  public ZookeeperModule(@GerritServerConfig Config config) {
    this.config = config;
  }

  @Override
  protected void configure() {
    if (ZkConfig.getZookeeperConfigName(config) != null) {
      bind(ZkRepositoryManager.class).in(SINGLETON);
      bind(GitRepositoryManager.class).to(ZkRepositoryManager.class);
      listener().to(ZkRepositoryManager.Lifecycle.class);
    }
  }
}
