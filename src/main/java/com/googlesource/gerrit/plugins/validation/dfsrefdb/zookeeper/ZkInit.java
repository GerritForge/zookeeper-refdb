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

import static com.googlesource.gerrit.plugins.validation.dfsrefdb.zookeeper.ZookeeperConfig.SECTION;
import static com.googlesource.gerrit.plugins.validation.dfsrefdb.zookeeper.ZookeeperConfig.SUBSECTION;

import com.google.gerrit.pgm.init.api.ConsoleUI;
import com.google.gerrit.pgm.init.api.InitStep;
import com.google.gerrit.pgm.init.api.Section;
import com.google.gerrit.server.schema.NoteDbSchemaVersionManager;
import com.google.inject.Inject;
import org.eclipse.jgit.lib.Config;

public class ZkInit implements InitStep {

  private final ConsoleUI ui;
  private final Config config;
  private final NoteDbSchemaVersionManager versionManager;

  @Inject
  ZkInit(ConsoleUI ui, Section.Factory sections, NoteDbSchemaVersionManager versionManager) {
    this.ui = ui;
    this.versionManager = versionManager;

    this.config = new Config();
    Section zkSection = sections.get(SECTION, SUBSECTION);
    for (String key : ZookeeperConfig.KEYS) {
      String value = zkSection.get(key);
      if (value != null) {
        config.setString(SECTION, SUBSECTION, key, value);
      }
    }
  }

  @Override
  public void run() throws Exception {
    if (config.getSections().isEmpty()) {
      ui.message("Zookeeper configuration not found: global-refdb migration skipped");
    }

    int schemaVersion = versionManager.read();
  }
}
