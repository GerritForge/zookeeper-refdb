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

import com.google.gerrit.pgm.init.api.ConsoleUI;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.apache.curator.framework.CuratorFramework;

public class ZkMigrations {
  private final Injector injector;
  private final ConsoleUI ui;

  @Inject
  public ZkMigrations(Injector injector, ConsoleUI ui) {
    this.injector = injector;
    this.ui = ui;
  }

  @SuppressWarnings("unchecked")
  public void migrate(CuratorFramework curator, int currentVersion) throws Exception {
    for (int version = currentVersion; version <= LATEST; version++) {
      try {
        Class<ZkMigration> migration =
            (Class<ZkMigration>) Class.forName(ZkMigration.class.getName() + "_Schema_" + version);

        ui.message("Running Zookeeper migration to Schema {}", version);
        injector.getInstance(migration).run(curator);
      } catch (ClassNotFoundException e) {
      }
    }
  }
}
