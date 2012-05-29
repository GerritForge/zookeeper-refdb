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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.retry.ExponentialBackoffRetry;

import org.eclipse.jgit.lib.BaseRepositoryBuilder;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RemoteConfig;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

/** Builder for {@link ZkRepository}s. */
public class ZkRepositoryBuilder extends
    BaseRepositoryBuilder<ZkRepositoryBuilder, ZkRepository> {
  private final List<RemoteConfig> remotes = Lists.newArrayList();

  private Repository delegate;

  private CuratorFramework client;

  private String root;

  private String name;

  private String localReplica;

  private Executor executor;

  public ZkRepositoryBuilder(Config config) {
    setConfig(config);
  }

  @Override
  public ZkRepositoryBuilder setup() throws IllegalArgumentException,
      IOException {
    checkArgument(executor != null, "No executor specified");
    setupReplication();
    setupZooKeeper();
    return self();
  }

  @Override
  public ZkRepository build() throws IOException {
    setup();
    checkArgument(name != null, "No name specified");
    return new ZkRepository(this, delegate);
  }

  public ZkRepositoryBuilder setCuratorFramework(CuratorFramework client) {
    this.client = checkNotNull(client);
    return self();
  }

  public ZkRepositoryBuilder setZooKeeperRoot(String root) {
    this.root = root;
    return self();
  }

  public ZkRepositoryBuilder setName(String name) {
    this.name = name;
    return self();
  }

  public ZkRepositoryBuilder setExecutor(Executor executor) {
    this.executor = executor;
    return self();
  }

  public ZkRepositoryBuilder setDelegate(Repository delegate) {
    this.delegate = delegate;
    return self();
  }

  public ZkRepositoryBuilder setLocalReplica(String localReplica) {
    this.localReplica = localReplica;
    return self();
  }

  public ZkRepositoryBuilder addRemote(RemoteConfig remote) {
    remotes.add(remote);
    return self();
  }

  String getName() {
    return name;
  }

  CuratorFramework getCuratorFramework() {
    return client;
  }

  String getLocalReplica() {
    return localReplica;
  }

  Executor getExecutor() {
    return executor;
  }

  Collection<RemoteConfig> getRemotes() {
    return Collections.unmodifiableList(remotes);
  }

  private void setupReplication() throws IOException {
    Config cfg = getConfig();
    if (localReplica == null) {
      checkArgument(cfg != null, "Either Config or localReplica must be set");
      localReplica = cfg.getString("refdb", "zookeeper", "localReplica");
      checkArgument(localReplica != null,
          "Config must specify refdb.zookeeper.localReplica");
    }
    try {
      List<RemoteConfig> cfgRemotes = RemoteConfig.getAllRemoteConfigs(cfg);
      checkArgument(!cfgRemotes.isEmpty() || !remotes.isEmpty(),
        "No remotes specified for replication");
      // TODO(dborowitz): Check that remotes in the server config are not
      // changed by the repo config, as that could break replication.
      if (remotes.isEmpty()) {
        remotes.addAll(cfgRemotes);
      }
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private void setupZooKeeper() throws IOException {
    if (client != null) {
      return;
    }
    Config cfg = getConfig();
    checkArgument(cfg != null,
        "Either Config or CuratorFramework must be set");
    String zkName = ZkConfig.getZookeeperConfigName(cfg);
    checkArgument(zkName != null, "Config must contain refdb.zookeeper");
    ZkConfig zkCfg = new ZkConfig(cfg, zkName);
    checkArgument(!zkCfg.getConnectString().isEmpty(),
        "zookeeper.%s contains no servers", zkName);
    client = CuratorFrameworkFactory.builder()
        .connectString(zkCfg.getConnectString())
        .sessionTimeoutMs(zkCfg.getSessionTimeoutMs())
        .connectionTimeoutMs(zkCfg.getConnectionTimeoutMs())
        .retryPolicy(new ExponentialBackoffRetry(40, 20))
        .namespace(Strings.nullToEmpty(root))
        .build();
  }
}
