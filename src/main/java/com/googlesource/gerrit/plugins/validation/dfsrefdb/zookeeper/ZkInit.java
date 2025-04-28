// Copyright (C) 2021 The Android Open Source Project
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

package com.googlesource.gerrit.plugins.validation.dfsrefdb.zookeeper;

import com.google.gerrit.common.Nullable;
import com.google.gerrit.exceptions.StorageException;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.pgm.init.api.ConsoleUI;
import com.google.gerrit.pgm.init.api.InitStep;
import com.google.gerrit.server.config.AllProjectsName;
import com.google.gerrit.server.config.AllProjectsNameProvider;
import com.google.gerrit.server.config.GlobalPluginConfigProvider;
import com.google.gerrit.server.schema.NoteDbSchemaVersionManager;
import com.google.gerrit.server.securestore.SecureStore;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.googlesource.gerrit.plugins.validation.dfsrefdb.zookeeper.migration.ZkMigrations;
import org.apache.curator.framework.CuratorFramework;
import org.eclipse.jgit.lib.Config;

@Singleton
public class ZkInit implements InitStep {

  private final ConsoleUI ui;
  private final Config config;

  @Inject(optional = true)
  @Nullable private NoteDbSchemaVersionManager versionManager;

  @Inject(optional = true)
  @Nullable private ZkMigrations zkMigrations;

  private final String pluginName;
  private final Injector initInjector;
  private final SecureStore secureStore;

  @Inject
  ZkInit(
      ConsoleUI ui,
      @PluginName String pluginName,
      Injector initInjector,
      SecureStore secureStore,
      GlobalPluginConfigProvider pluginConfigProvider) {
    this.ui = ui;
    this.pluginName = pluginName;
    this.initInjector = initInjector;
    this.secureStore = secureStore;
    this.config = new ZkMergedConfig(pluginConfigProvider.get(pluginName));
  }

  @Override
  public void run() throws Exception {
    if (config.getSections().isEmpty()) {
      ui.message("%s configuration not found: global-refdb migration skipped\n", pluginName);
      return;
    }

    Injector injector = getInjector(initInjector);
    injector.injectMembers(this);

    if (zkMigrations != null && versionManager != null) {
      try {
        try (CuratorFramework curator = new ZookeeperConfig(config).startCurator()) {
          zkMigrations.migrate(injector, curator, versionManager.read());
        }
      } catch (StorageException e) {
        // No version information: skip migration as it is most likely a new site
      }
    }
  }

  public static Injector getInjector(Injector parentInjector) {
    return parentInjector.createChildInjector(
        new AbstractModule() {
          @Override
          protected void configure() {
            bind(AllProjectsName.class).toProvider(AllProjectsNameProvider.class);
          }
        });
  }

  private class ZkMergedConfig extends Config {
    ZkMergedConfig(Config baseConfig) {
      super(baseConfig);
    }

    @Override
    public String getString(String section, String subsection, String name) {
      String secure = secureStore.getForPlugin(pluginName, section, subsection, name);
      if (secure != null) {
        return secure;
      }
      return super.getString(section, subsection, name);
    }
  }
}
