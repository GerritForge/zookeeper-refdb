package com.googlesource.gerrit.plugins.validation.dfsrefdb.zookeeper;

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
