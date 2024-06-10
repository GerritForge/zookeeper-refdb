package com.googlesource.gerrit.plugins.validation.dfsrefdb.zookeeper;

import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.NullProgressMonitor;
import org.eclipse.jgit.transport.RefSpec;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.Mockito;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static com.google.common.truth.Truth.assertThat;
import static com.google.gerrit.testing.GerritJUnit.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ZookeeperACLParsingTest {
  @Rule
  public TestName nameRule = new TestName();

  String PLUGIN_NAME = "zookeeper";

  @Test
  public void shouldNotSetUpAuthsIfNotProvided() {

    PluginConfigFactory cfgFactory = mock(PluginConfigFactory.class);
    when(cfgFactory.getGlobalPluginConfig(PLUGIN_NAME)).thenReturn(new Config());

    ZookeeperConfig configuration = new ZookeeperConfig(cfgFactory, PLUGIN_NAME);

    CuratorFrameworkFactory.Builder builder = configuration.parseCuratorConfig();

    assertThat(builder.getAuthInfos()).isNull();
  }

  @Test
  public void shouldCorrectlyParseAuthConfigIfProvided() {
    Config pluginConfig = new Config();
    pluginConfig.setString("ref-database", ZookeeperConfig.SUBSECTION, ZookeeperConfig.KEY_USERNAME, "globalrefdb");
    pluginConfig.setString("ref-database", ZookeeperConfig.SUBSECTION, ZookeeperConfig.KEY_PASSWORD, "globalrefdb-secret");

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
    pluginConfig.setString("ref-database", ZookeeperConfig.SUBSECTION, ZookeeperConfig.KEY_PASSWORD, "pwd");

    PluginConfigFactory cfgFactory = mock(PluginConfigFactory.class);
    when(cfgFactory.getGlobalPluginConfig(PLUGIN_NAME)).thenReturn(pluginConfig);

    ZookeeperConfig configuration = new ZookeeperConfig(cfgFactory, PLUGIN_NAME);

    Exception e = assertThrows(IllegalArgumentException.class, () -> configuration.parseCuratorConfig());
    assertTrue(e.getMessage().contains("Only one between password or username for Zookeeper was set, please set both to succesfully authenticate"));
  }

  @Test
  public void shouldNotParseAuthConfigIfPasswordIsNotProvided() {
    Config pluginConfig = new Config();
    pluginConfig.setString("ref-database", ZookeeperConfig.SUBSECTION, ZookeeperConfig.KEY_USERNAME, "usr");

    PluginConfigFactory cfgFactory = mock(PluginConfigFactory.class);
    when(cfgFactory.getGlobalPluginConfig(PLUGIN_NAME)).thenReturn(pluginConfig);

    ZookeeperConfig configuration = new ZookeeperConfig(cfgFactory, PLUGIN_NAME);

    Exception e = assertThrows(IllegalArgumentException.class, () -> configuration.parseCuratorConfig());
    assertTrue(e.getMessage().contains("Only one between password or username for Zookeeper was set, please set both to succesfully authenticate"));
  }
}
