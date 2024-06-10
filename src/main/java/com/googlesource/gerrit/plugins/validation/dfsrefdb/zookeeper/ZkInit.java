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

import com.google.gerrit.exceptions.StorageException;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.pgm.init.api.ConsoleUI;
import com.google.gerrit.pgm.init.api.InitStep;
import com.google.gerrit.server.config.AllProjectsName;
import com.google.gerrit.server.config.AllProjectsNameProvider;
import com.google.gerrit.server.config.SitePaths;
import com.google.gerrit.server.schema.NoteDbSchemaVersionManager;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.googlesource.gerrit.plugins.validation.dfsrefdb.zookeeper.migration.ZkMigrations;
import java.io.IOException;
import org.apache.curator.framework.CuratorFramework;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;

@Singleton
public class ZkInit implements InitStep {

  private final ConsoleUI ui;
  private final FileBasedConfig config;

  @Inject(optional = true)
  private NoteDbSchemaVersionManager versionManager;

  @Inject(optional = true)
  private ZkMigrations zkMigrations;

  private final String pluginName;
  private final Injector initInjector;

  @Inject
  ZkInit(ConsoleUI ui, SitePaths site, @PluginName String pluginName, Injector initInjector)
      throws IOException, ConfigInvalidException {
    this.ui = ui;
    this.pluginName = pluginName;
    this.initInjector = initInjector;

    config =
        new FileBasedConfig(site.etc_dir.resolve(pluginName + ".config").toFile(), FS.DETECTED);
    config.load();
  }

  @Override
  public void run() throws Exception {
    if (config.getSections().isEmpty()) {
      ui.message("%s configuration not found: global-refdb migration skipped\n", pluginName);
      return;
    }

    Injector injector = getInjector(initInjector);
    injector.injectMembers(this);

    try {
      try (CuratorFramework curator = new ZookeeperConfig(config).startCurator()) {
        zkMigrations.migrate(injector, curator, versionManager.read());
      }
    } catch (StorageException e) {
      // No version information: skip migration as it is most likely a new site
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
}
