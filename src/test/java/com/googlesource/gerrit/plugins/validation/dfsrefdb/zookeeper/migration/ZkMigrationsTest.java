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

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.google.gerrit.acceptance.LightweightPluginDaemonTest;
import com.google.gerrit.acceptance.TestPlugin;
import com.google.gerrit.pgm.init.api.ConsoleUI;
import org.apache.curator.framework.CuratorFramework;
import org.junit.Before;
import org.junit.Test;

@TestPlugin(
    name = "foo",
    sysModule = "com.googlesource.gerrit.plugins.validation.dfsrefdb.zookeeper.ZkValidationModule")
public class ZkMigrationsTest extends LightweightPluginDaemonTest {

  private CuratorFramework curatorMock;
  private ConsoleUI uiMock;
  private ZkMigrations migrations;

  @Before
  public void setupMocks() {
    curatorMock = mock(CuratorFramework.class, RETURNS_DEEP_STUBS);
    uiMock = mock(ConsoleUI.class);
    migrations = new ZkMigrations(uiMock, "foo");
  }

  @Test
  public void shouldNotMigrateAnythingForNewSites() throws Exception {
    migrations.migrate(plugin.getSysInjector(), curatorMock, 0);
    verifyNoMoreInteractions(curatorMock);
  }

  @Test
  public void shouldCallCuratorDeleteForMigrationToSchema182() throws Exception {
    migrations.migrate(plugin.getSysInjector(), curatorMock, 182);
    verify(curatorMock).delete();
  }

  @Test
  public void shouldNotCallCuratorDeleteForSchema183() throws Exception {
    migrations.migrate(plugin.getSysInjector(), curatorMock, 183);
    verifyNoMoreInteractions(curatorMock);
  }
}
