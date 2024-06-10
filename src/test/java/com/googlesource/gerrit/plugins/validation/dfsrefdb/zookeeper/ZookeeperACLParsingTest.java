package com.googlesource.gerrit.plugins.validation.dfsrefdb.zookeeper;

import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.eclipse.jgit.lib.Config;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.Mockito;
import java.nio.charset.StandardCharsets;
import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.*;

public class ZookeeperACLParsingTest {
  @Rule
  public TestName nameRule = new TestName();

  String PLUGIN_NAME = "zookeeper";

  @Test
  public void shouldNotSetUpAuthsIfNotProvided() {

    PluginConfigFactory cfgFactory = mock(PluginConfigFactory.class);
    when(cfgFactory.getGlobalPluginConfig(PLUGIN_NAME)).thenReturn(new Config());

    PluginConfigFactory configFactory = Mockito.mock(PluginConfigFactory.class);
    Mockito.when(configFactory.getFromGerritConfig(PLUGIN_NAME))
        .thenReturn(PluginConfig.createFromGerritConfig(PLUGIN_NAME, new Config()));
    ZookeeperConfig configuration = new ZookeeperConfig(new Config(), cfgFactory, PLUGIN_NAME);

    CuratorFrameworkFactory.Builder builder = configuration.parseCuratorConfig();

    assertThat(builder.getAuthInfos()).isNull();
  }

  @Test
  public void shouldCorrectlyParseAuthConfigIfProvided() {
    Config secureConfig = new Config();
    secureConfig.setString("ref-database", ZookeeperConfig.SUBSECTION, ZookeeperConfig.KEY_USERNAME, "globalrefdb");
    secureConfig.setString("ref-database", ZookeeperConfig.SUBSECTION, ZookeeperConfig.KEY_PASSWORD, "globalrefdb-secret");

    PluginConfigFactory cfgFactory = mock(PluginConfigFactory.class);
    when(cfgFactory.getGlobalPluginConfig(PLUGIN_NAME)).thenReturn(new Config());

    PluginConfigFactory configFactory = Mockito.mock(PluginConfigFactory.class);
    Mockito.when(configFactory.getFromGerritConfig(PLUGIN_NAME))
        .thenReturn(PluginConfig.createFromGerritConfig(PLUGIN_NAME, secureConfig));
    ZookeeperConfig configuration = new ZookeeperConfig(secureConfig, cfgFactory, PLUGIN_NAME);

    CuratorFrameworkFactory.Builder builder = configuration.parseCuratorConfig();

    String authInfo = new String(builder.getAuthInfos().get(0).getAuth(), StandardCharsets.UTF_8);

    assertThat(authInfo).isEqualTo("globalrefdb:globalrefdb-secret");
  }
}
