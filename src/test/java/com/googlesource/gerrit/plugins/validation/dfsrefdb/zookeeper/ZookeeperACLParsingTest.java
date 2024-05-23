package com.googlesource.gerrit.plugins.validation.dfsrefdb.zookeeper;

import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.config.SitePaths;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.*;

public class ZookeeperACLParsingTest {

  public static String PLUGIN_NAME = "zookeeper";
  @Rule
  public TestName nameRule = new TestName();

  @Test
  public void shouldNotSetUpAuthsIfNotProvided() throws Exception {

    PluginConfigFactory cfgFactory = mock(PluginConfigFactory.class);
    when(cfgFactory.getGlobalPluginConfig(PLUGIN_NAME)).thenReturn(new Config());

    final Path root = ZookeeperConfigBase.randomTestSite();
    final SitePaths sitePaths = new SitePaths(root);

    ZookeeperConfig zookeeperConfig = new ZookeeperConfig(sitePaths, cfgFactory, PLUGIN_NAME);

    CuratorFrameworkFactory.Builder builder = zookeeperConfig.parseCuratorConfig();

    assertThat(builder.getAuthInfos()).isNull();
  }

  @Test
  public void shouldCorrectlyParseAuthConfigIfProvided() throws Exception {
    SitePaths sitePaths = setupSitePath();

    StoredConfig secureConfig =
        new FileBasedConfig(
            sitePaths.resolve("etc").resolve("secure.config").toFile(), FS.DETECTED);
    secureConfig.load();
    secureConfig.setString("ref-database", "zookeeper", "username", "globalrefdb");
    secureConfig.setString("ref-database", "zookeeper", "password", "globalrefdb-secret");
    secureConfig.save();

    PluginConfigFactory cfgFactory = mock(PluginConfigFactory.class);
    when(cfgFactory.getGlobalPluginConfig(PLUGIN_NAME)).thenReturn(new Config());

    ZookeeperConfig configuration = new ZookeeperConfig(sitePaths, cfgFactory, PLUGIN_NAME);
    CuratorFrameworkFactory.Builder builder = configuration.parseCuratorConfig();
    String authInfo = new String(builder.getAuthInfos().get(0).getAuth(), StandardCharsets.UTF_8);

    assertThat(authInfo).isEqualTo("globalrefdb:globalrefdb-secret");
  }

  @Test
  public void shouldNotParseAuthConfigIfOnlyUsernameIsProvided() throws Exception {
    SitePaths sitePaths = setupSitePath();

    StoredConfig secureConfig =
        new FileBasedConfig(
            sitePaths.resolve("etc").resolve("secure.config").toFile(), FS.DETECTED);
    secureConfig.load();
    secureConfig.setString("ref-database", "zookeeper", "username", "globalrefdb");
    secureConfig.save();

    PluginConfigFactory cfgFactory = mock(PluginConfigFactory.class);
    when(cfgFactory.getGlobalPluginConfig(PLUGIN_NAME)).thenReturn(new Config());

    ZookeeperConfig configuration = new ZookeeperConfig(sitePaths, cfgFactory, PLUGIN_NAME);
    CuratorFrameworkFactory.Builder builder = configuration.parseCuratorConfig();

    assertThat(builder.getAuthInfos()).isNull();
  }

  @Test
  public void shouldNotParseAuthConfigIfOnlyPasswordIsProvided() throws Exception {
    SitePaths sitePaths = setupSitePath();

    StoredConfig secureConfig =
        new FileBasedConfig(
            sitePaths.resolve("etc").resolve("secure.config").toFile(), FS.DETECTED);
    secureConfig.load();
    secureConfig.setString("ref-database", "zookeeper", "password", "globalrefdb");
    secureConfig.save();

    PluginConfigFactory cfgFactory = mock(PluginConfigFactory.class);
    when(cfgFactory.getGlobalPluginConfig(PLUGIN_NAME)).thenReturn(new Config());

    ZookeeperConfig configuration = new ZookeeperConfig(sitePaths, cfgFactory, PLUGIN_NAME);
    CuratorFrameworkFactory.Builder builder = configuration.parseCuratorConfig();

    assertThat(builder.getAuthInfos()).isNull();
  }

  private SitePaths setupSitePath() throws IOException{
    final Path root = ZookeeperConfigBase.randomTestSite();
    final SitePaths sitePaths = new SitePaths(root);
    Files.createDirectories(sitePaths.etc_dir);

    return sitePaths;
  }
}
