// Copyright (C) 2019 The Android Open Source Project
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

import static com.google.common.truth.Truth.assertThat;
import static com.google.gerrit.testing.GerritJUnit.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.gerrit.server.config.PluginConfigFactory;
import java.nio.charset.StandardCharsets;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.eclipse.jgit.lib.Config;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class ZookeeperACLParsingTest {
  @Rule public TestName nameRule = new TestName();

  String PLUGIN_NAME = "zookeeper";

  @Test
  public void shouldNotSetUpAuthIfNotProvided() {

    PluginConfigFactory cfgFactory = mock(PluginConfigFactory.class);
    when(cfgFactory.getGlobalPluginConfig(PLUGIN_NAME)).thenReturn(new Config());

    ZookeeperConfig configuration = new ZookeeperConfig(cfgFactory, PLUGIN_NAME);

    CuratorFrameworkFactory.Builder builder = configuration.parseCuratorConfig();

    assertThat(builder.getAuthInfos()).isNull();
  }

  @Test
  public void shouldCorrectlyParseAuthConfigIfProvided() {
    Config pluginConfig = new Config();
    pluginConfig.setString(
        "ref-database", ZookeeperConfig.SUBSECTION, ZookeeperConfig.KEY_USERNAME, "globalrefdb");
    pluginConfig.setString(
        "ref-database",
        ZookeeperConfig.SUBSECTION,
        ZookeeperConfig.KEY_PASSWORD,
        "globalrefdb-secret");

    PluginConfigFactory cfgFactory = mock(PluginConfigFactory.class);
    when(cfgFactory.getGlobalPluginConfig(PLUGIN_NAME)).thenReturn(pluginConfig);

    ZookeeperConfig configuration = new ZookeeperConfig(cfgFactory, PLUGIN_NAME);

    CuratorFrameworkFactory.Builder builder = configuration.parseCuratorConfig();

    String authInfo = new String(builder.getAuthInfos().get(0).getAuth(), StandardCharsets.UTF_8);

    assertThat(authInfo).isEqualTo("globalrefdb:globalrefdb-secret");
  }

  @Test
  public void shouldNotParseAuthConfigIfUsernameIsNotProvided() {
    Config pluginConfig = new Config();
    pluginConfig.setString(
        "ref-database", ZookeeperConfig.SUBSECTION, ZookeeperConfig.KEY_PASSWORD, "pwd");

    PluginConfigFactory cfgFactory = mock(PluginConfigFactory.class);
    when(cfgFactory.getGlobalPluginConfig(PLUGIN_NAME)).thenReturn(pluginConfig);

    ZookeeperConfig configuration = new ZookeeperConfig(cfgFactory, PLUGIN_NAME);

    Exception e =
        assertThrows(IllegalArgumentException.class, () -> configuration.parseCuratorConfig());
    assertTrue(
        e.getMessage()
            .contains(
                "Only one between password or username for Zookeeper was set, please set both to successfully authenticate"));
  }

  @Test
  public void shouldNotParseAuthConfigIfPasswordIsNotProvided() {
    Config pluginConfig = new Config();
    pluginConfig.setString(
        "ref-database", ZookeeperConfig.SUBSECTION, ZookeeperConfig.KEY_USERNAME, "usr");

    PluginConfigFactory cfgFactory = mock(PluginConfigFactory.class);
    when(cfgFactory.getGlobalPluginConfig(PLUGIN_NAME)).thenReturn(pluginConfig);

    ZookeeperConfig configuration = new ZookeeperConfig(cfgFactory, PLUGIN_NAME);

    Exception e =
        assertThrows(IllegalArgumentException.class, () -> configuration.parseCuratorConfig());
    assertTrue(
        e.getMessage()
            .contains(
                "Only one between password or username for Zookeeper was set, please set both to successfully authenticate"));
  }
}
