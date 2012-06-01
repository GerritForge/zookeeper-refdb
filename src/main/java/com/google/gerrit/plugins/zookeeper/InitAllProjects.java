// Copyright (C) 2012 The Android Open Source Project
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

package com.google.gerrit.plugins.zookeeper;

import static com.google.gerrit.server.schema.DataSourceProvider.Context.SINGLE_USER;
import static com.google.inject.Scopes.SINGLETON;
import static com.google.inject.Stage.PRODUCTION;

import com.google.gerrit.lifecycle.LifecycleManager;
import com.google.gerrit.lifecycle.LifecycleModule;
import com.google.gerrit.server.config.AllProjectsName;
import com.google.gerrit.server.config.GerritServerConfigModule;
import com.google.gerrit.server.config.SiteInfoModule;
import com.google.gerrit.server.config.SitePath;
import com.google.gerrit.server.plugins.ServerInformationImpl;
import com.google.gerrit.server.schema.DataSourceProvider;
import com.google.gerrit.server.schema.DatabaseModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

/**
 * Standalone binary to initialize All-Projects in Zookeeper.
 * <p>
 * This is <em>only</em> for All-Projects, which cannot be created via the
 * <code>zookeeper-refdb create</code> command, because the site must be loaded
 * before a plugin command can be run.
 *
 * @author dborowitz@google.com (Dave Borowitz)
 */
public class InitAllProjects {
  @Option(name = "--site-path", aliases = {"-d"}, usage = "Local directory containing site data")
  private File sitePath = new File(".");

  public static void main(String[] argv) throws Exception {
    System.exit(new InitAllProjects().mainImpl(argv));
  }

  private final LifecycleManager manager = new LifecycleManager();
  private Injector sysInjector;

  public int mainImpl(String[] argv) throws Exception {
    new CmdLineParser(this).parseArgument(argv);
    mustHaveValidSite();
    createSysInjector();
    manager.add(sysInjector);
    manager.start();
    try {
      CreateCommand.create(
          sysInjector.getInstance(ZkRepositoryManager.class),
          sysInjector.getInstance(AllProjectsName.class),
          new PrintWriter(System.out));
      return 0;
    } finally {
      manager.stop();
    }
  }

  private File getSitePath() {
    File path = sitePath.getAbsoluteFile();
    if (".".equals(path.getName())) {
      path = path.getParentFile();
    }
    return path;
  }

  private void mustHaveValidSite() {
    if (!new File(new File(getSitePath(), "etc"), "gerrit.config").exists()) {
      throw new IllegalStateException("not a Gerrit site: '" + getSitePath()
          + "'\nPerhaps you need to run init first?");
    }
  }

  private void createSysInjector() {
    final File sitePath = getSitePath();
    List<Module> modules = new ArrayList<Module>();
    modules.add(new LifecycleModule() {
      @Override
      protected void configure() {
        bind(File.class).annotatedWith(SitePath.class).toInstance(sitePath);
        bind(DataSourceProvider.Context.class).toInstance(SINGLE_USER);
        bind(Key.get(DataSource.class, Names.named("ReviewDb"))).toProvider(
            DataSourceProvider.class).in(SINGLETON);
        listener().to(DataSourceProvider.class);
      }
    });
    modules.add(new GerritServerConfigModule());
    modules.add(new DatabaseModule());
    modules.add(new SiteInfoModule());
    modules.add(new ServerInformationImpl.Module());
    Injector dbInjector = Guice.createInjector(PRODUCTION, modules);
    sysInjector = dbInjector.createChildInjector(
        dbInjector.getInstance(ZookeeperModule.class));
  }
}
