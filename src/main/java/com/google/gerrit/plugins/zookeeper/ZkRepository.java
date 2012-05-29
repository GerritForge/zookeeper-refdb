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

import org.eclipse.jgit.lib.ObjectDatabase;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.ReflogReader;

import java.io.IOException;

/** Repository backed by a local repository and Zookeeper for ref updates. */
public class ZkRepository extends Repository {
  private final Repository delegate;
  private final ZkRefDatabase refdb;

  ZkRepository(ZkRepositoryBuilder options, Repository delegate)
      throws IOException {
    super(options);
    if (delegate != null) {
      this.delegate = delegate;
    } else {
      this.delegate = new FileRepository(options);
    }
    this.refdb = new ZkRefDatabase(options.getCuratorFramework(),
        options.getName(), this,
        delegate.getRefDatabase(), options.getLocalReplica(),
        options.getExecutor(), options.getRemotes());
  }

  @Override
  public void create(boolean bare) throws IOException {
    // Note: assumes delegate is already created.
    refdb.create();
  }

  @Override
  public ObjectDatabase getObjectDatabase() {
    return delegate.getObjectDatabase();
  }

  @Override
  public ZkRefDatabase getRefDatabase() {
    return refdb;
  }

  @Override
  public StoredConfig getConfig() {
    return delegate.getConfig();
  }

  @Override
  public void scanForRepoChanges() throws IOException {
    delegate.scanForRepoChanges();
    refdb.scanAllRefs();
  }

  @Override
  public void notifyIndexChanged() {
    delegate.notifyIndexChanged();
  }

  @Override
  public ReflogReader getReflogReader(String refName) throws IOException {
    return delegate.getReflogReader(refName);
  }

  @Override
  protected void doClose() {
    delegate.close();
    refdb.close();
  }
}
