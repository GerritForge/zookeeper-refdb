package com.googlesource.gerrit.plugins.validation.dfsrefdb.zookeeper;

// Copyright (C) 2011 The Android Open Source Project
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

import com.google.gerrit.server.config.SitePaths;
import com.google.inject.Inject;

import java.io.IOException;
import java.util.Optional;

import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Looks up zookeeper's password in secure.config.
 */
public class SecureCredentialsFactory {
  public static final String KEY_USERNAME = "username";
  public static final String KEY_PASSWORD = "password";
  private final Config config;
  private static final Logger log = LoggerFactory.getLogger(ZookeeperConfig.class);

  @Inject
  public SecureCredentialsFactory(SitePaths site) throws ConfigInvalidException, IOException {
    config = loadConfig(site);
  }

  private static Config loadConfig(SitePaths site) throws ConfigInvalidException, IOException {
    FileBasedConfig cfg = new FileBasedConfig(site.secure_config.toFile(), FS.DETECTED);
    if (cfg.getFile().exists() && cfg.getFile().length() > 0) {
      try {
        cfg.load();
      } catch (ConfigInvalidException e) {
        throw new ConfigInvalidException(
            String.format("Config file %s is invalid: %s", cfg.getFile(), e.getMessage()), e);
      } catch (IOException e) {
        throw new IOException(
            String.format("Cannot read %s: %s", cfg.getFile(), e.getMessage()), e);
      }
    }
    return cfg;
  }

  public Optional<String> loadCredentials() {
    String user = config.getString(ZookeeperConfig.SECTION, ZookeeperConfig.SUBSECTION, KEY_USERNAME);
    String pass = config.getString(ZookeeperConfig.SECTION, ZookeeperConfig.SUBSECTION, KEY_PASSWORD);

    if(user == null && pass == null) {
      return Optional.empty();
    } else if ((user == null && pass != null) || (user != null && pass == null)) {
      log.error("Only one between username and password was configured, please configure both to succesfully authenticate to Zookeeper");
      return Optional.empty();
    } else {
      return Optional.of(user + ":" + pass);
    }
  }
}

