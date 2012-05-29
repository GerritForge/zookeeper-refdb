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

import com.google.gerrit.extensions.events.LifecycleListener;
import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.gerrit.server.git.GitRepositoryManager;
import com.google.gerrit.server.git.LocalDiskRepositoryManager;
import com.google.gerrit.server.git.RepositoryCaseMismatchException;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.netflix.curator.framework.CuratorFramework;

import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;

import java.io.IOException;
import java.util.SortedSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/** Repo manager that opens {@link ZkRepository}s. */
@Singleton
public class ZkRepositoryManager implements GitRepositoryManager {
  public static class Lifecycle implements LifecycleListener {
    private final ZkRepositoryManager repoManager;

    @Inject
    Lifecycle(ZkRepositoryManager repoManager) {
      this.repoManager = repoManager;
    }

    @Override
    public void start() {
      repoManager.client.start();
    }

    @Override
    public void stop() {
      repoManager.client.close();
    }
  }

  private final LocalDiskRepositoryManager delegate;
  private final Executor executor;
  private final String zkRoot;
  private final Config config;
  private final CuratorFramework client;

  @Inject
  ZkRepositoryManager(@GerritServerConfig Config config,
      LocalDiskRepositoryManager delegate) throws IOException {
    this.config = config;
    // TODO(dborowitz): Configure executor.
    this.executor = Executors.newFixedThreadPool(1);
    this.zkRoot = ZkConfig.getZookeeperRoot(config);
    this.delegate = delegate;

    this.client = new ZkRepositoryBuilder(config)
        .setExecutor(executor)
        .setZooKeeperRoot(zkRoot)
        .setup()
        .getCuratorFramework();
  }

  @Override
  public Repository openRepository(Project.NameKey name)
      throws RepositoryNotFoundException, IOException {
    return RepositoryCache.open(new Key(name), true);
  }

  @Override
  public Repository createRepository(Project.NameKey name)
      throws RepositoryCaseMismatchException, RepositoryNotFoundException,
      IOException {
    return RepositoryCache.open(new Key(name), false);
  }

  @Override
  public SortedSet<Project.NameKey> list() {
    return delegate.list();
  }

  @Override
  public String getProjectDescription(Project.NameKey name)
      throws RepositoryNotFoundException, IOException {
    return delegate.getProjectDescription(name);
  }

  @Override
  public void setProjectDescription(Project.NameKey name, String description) {
    delegate.setProjectDescription(name, description);
  }

  private class Key implements RepositoryCache.Key {
    private final Project.NameKey name;

    private Key(Project.NameKey name) {
      this.name = name;
    }

    @Override
    public Repository open(boolean mustExist) throws IOException,
        RepositoryNotFoundException {
      ZkRepositoryBuilder builder = new ZkRepositoryBuilder(config)
          .setExecutor(executor)
          .setZooKeeperRoot(zkRoot)
          .setCuratorFramework(client)
          .setName(name.get());
      if (mustExist) {
        ZkRepository repo = builder.setDelegate(delegate.openRepository(name))
            .build();
        if (!repo.getRefDatabase().exists()) {
          throw new RepositoryNotFoundException(name.get());
        }
        repo.getRefDatabase().start();
        return repo;
      } else {
        Repository delegateRepo;
        try {
          delegateRepo = delegate.openRepository(name);
        } catch (RepositoryNotFoundException e) {
          delegateRepo = delegate.createRepository(name);
        }

        ZkRepository repo = builder.setDelegate(delegateRepo).build();
        repo.create();
        repo.getRefDatabase().start();
        return repo;
      }
    }

    @Override
    public int hashCode() {
      return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof Key && name.equals(((Key) o).name);
    }
  }
}
