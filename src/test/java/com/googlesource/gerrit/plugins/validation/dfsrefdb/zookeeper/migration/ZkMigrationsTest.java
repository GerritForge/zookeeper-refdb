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
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.google.gerrit.acceptance.LightweightPluginDaemonTest;
import com.google.gerrit.acceptance.TestPlugin;
import com.google.gerrit.entities.RefNames;
import com.google.gerrit.pgm.init.api.ConsoleUI;
import com.google.gerrit.server.schema.NoteDbSchemaVersions;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundVersionable;
import org.apache.curator.framework.api.DeleteBuilder;
import org.junit.Before;
import org.junit.Test;

@TestPlugin(
    name = "foo",
    sysModule = "com.googlesource.gerrit.plugins.validation.dfsrefdb.zookeeper.ZkValidationModule")
public class ZkMigrationsTest extends LightweightPluginDaemonTest {

  private CuratorFramework curatorMock;
  private ConsoleUI uiMock;
  private ZkMigrations migrations;
  private DeleteBuilder deleteBuilderMock;
  private BackgroundVersionable backgroundVersionableMock;

  @Before
  public void setup() {
    curatorMock = mock(CuratorFramework.class, RETURNS_DEEP_STUBS);
    deleteBuilderMock = mock(DeleteBuilder.class);
    backgroundVersionableMock = mock(BackgroundVersionable.class);
    when(curatorMock.delete()).thenReturn(deleteBuilderMock);
    when(deleteBuilderMock.deletingChildrenIfNeeded()).thenReturn(backgroundVersionableMock);

    uiMock = mock(ConsoleUI.class);
    migrations = new ZkMigrations(uiMock, "foo");
  }

  @Test
  public void shouldNotMigrateAnythingForNewSites() throws Exception {
    migrations.migrate(plugin.getSysInjector(), curatorMock, 0);
    verifyZeroInteractions(curatorMock);
  }

  @Test
  public void shouldNotMigrateAnythingForSchema184() throws Exception {
    migrations.migrate(plugin.getSysInjector(), curatorMock, 184);
    verifyZeroInteractions(curatorMock);
  }

  @Test
  public void shouldCallCuratorDeleteForMigrationFromSchema182PlusOneToLatest() throws Exception {
    migrations.migrate(plugin.getSysInjector(), curatorMock, 182);

    verify(curatorMock).delete();
    verify(deleteBuilderMock).forPath(eq("/All-Projects/" + RefNames.REFS_CONFIG));

    verifyNoMoreInteractions(curatorMock);
    verifyNoMoreInteractions(deleteBuilderMock);
    verifyZeroInteractions(backgroundVersionableMock);
  }

  @Test
  public void shouldNotCallCuratorDeleteIfAlreadyOnLatestVersion() throws Exception {
    migrations.migrate(plugin.getSysInjector(), curatorMock, NoteDbSchemaVersions.LATEST);
    verifyZeroInteractions(curatorMock);
  }

  @Test
  public void shouldNotCallCuratorDeleteIfOverLatestVersion() throws Exception {
    migrations.migrate(plugin.getSysInjector(), curatorMock, NoteDbSchemaVersions.LATEST + 1);
    verifyZeroInteractions(curatorMock);
  }
}
