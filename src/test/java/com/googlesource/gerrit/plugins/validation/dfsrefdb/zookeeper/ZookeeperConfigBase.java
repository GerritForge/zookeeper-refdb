package com.googlesource.gerrit.plugins.validation.dfsrefdb.zookeeper;

import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.config.SitePaths;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ZookeeperConfigBase {

  public static Path randomTestSite() throws IOException {
    Path tmp = Files.createTempFile("gerrit_test_", "_site");
    Files.deleteIfExists(tmp);
    return tmp;
  }
}
