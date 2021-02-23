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

package com.googlesource.gerrit.plugins.validation.dfsrefdb.zookeeper.migration;

import static com.google.gerrit.server.schema.NoteDbSchemaVersions.LATEST;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.pgm.init.api.ConsoleUI;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.apache.curator.framework.CuratorFramework;

public class ZkMigrations {
  private final ConsoleUI ui;
  private final String pluginName;

  @Inject
  public ZkMigrations(ConsoleUI ui, @PluginName String pluginName) {
    this.ui = ui;
    this.pluginName = pluginName;
  }

  @SuppressWarnings("unchecked")
  public void migrate(Injector injector, CuratorFramework curator, int currentVersion)
      throws Exception {
    if (currentVersion <= 0) {
      return;
    }

    boolean headerDisplayed = false;

    for (int version = currentVersion; version <= LATEST; version++) {
      try {
        Class<ZkMigration> migration =
            (Class<ZkMigration>) Class.forName(ZkMigration.class.getName() + "_Schema_" + version);

        if (!headerDisplayed) {
          ui.header("%s migration", pluginName);
          headerDisplayed = true;
        }

        ui.message("Migrating %s data to schema %d ... ", pluginName, version);
        injector.getInstance(migration).run(curator);
        ui.message("DONE\n");
      } catch (ClassNotFoundException e) {
        // No migration found for the schema
      }
    }

    if (headerDisplayed) {
      ui.message("\n%s migration completed successfully\n\n", pluginName);
    }
  }
}
