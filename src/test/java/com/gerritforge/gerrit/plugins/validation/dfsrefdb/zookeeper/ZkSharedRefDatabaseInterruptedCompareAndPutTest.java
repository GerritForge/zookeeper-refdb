// Copyright (C) 2026 GerritForge, Inc.
//
// Licensed under the BSL 1.1 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// https://mariadb.com/bsl11/
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.gerritforge.gerrit.plugins.validation.dfsrefdb.zookeeper;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import com.gerritforge.gerrit.globalrefdb.GlobalRefDbSystemError;
import com.google.gerrit.extensions.registration.DynamicSet;
import java.io.IOException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectIdRef;
import org.eclipse.jgit.lib.Ref;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ZkSharedRefDatabaseInterruptedCompareAndPutTest implements RefFixture {
  private final Ref oldRef =
      new ObjectIdRef.Unpeeled(Ref.Storage.NETWORK, aBranchRef(), AN_OBJECT_ID_1);
  private final ObjectId newValue = AN_OBJECT_ID_2;
  private final String path =
      ZkSharedRefDatabase.pathFor(A_TEST_PROJECT_NAME_KEY, oldRef.getName());

  private TestingServer server;
  private CuratorFramework client;
  private ZkSharedRefDatabase refDb;
  private ObjectId valueToWriteBeforeInterruption;

  @Before
  public void setup() throws Exception {
    RetryOneTime retryPolicy = new RetryOneTime(/* sleepMsBetweenRetry */ 0);
    server = new TestingServer();
    client =
        CuratorFrameworkFactory.builder()
            .connectString(server.getConnectString())
            .retryPolicy(retryPolicy)
            .zookeeperFactory(InterruptedSetDataZooKeeper::new)
            .build();
    client.start();
    client
        .create()
        .creatingParentsIfNeeded()
        .forPath(path, ZkSharedRefDatabase.writeObjectId(oldRef.getObjectId()));
    refDb =
        new ZkSharedRefDatabase(
            client,
            new ZkConnectionConfig(retryPolicy, /* transactionLockTimeout */ 1000L),
            new StringDeserializerFactory(new DynamicSet<>()));
  }

  @After
  public void cleanup() throws IOException {
    try {
      client.close();
    } finally {
      server.close();
    }
  }

  @Test
  public void shouldReturnTrueWhenInterruptedCompareAndPutAlreadyWroteNewValue() throws Exception {
    valueToWriteBeforeInterruption = newValue;

    assertThat(refDb.compareAndPut(A_TEST_PROJECT_NAME_KEY, oldRef, newValue)).isTrue();

    assertThat(readStoredValue()).isEqualTo(newValue);
  }

  @Test
  public void shouldReturnFalseWhenInterruptedCompareAndPutDidntWriteNewValue() throws Exception {
    // Interrupt before writing anything. ZooKeeper still contains the old .
    assertThat(refDb.compareAndPut(A_TEST_PROJECT_NAME_KEY, oldRef, newValue)).isFalse();

    assertThat(readStoredValue()).isEqualTo(oldRef.getObjectId());
  }

  @Test
  public void shouldThrowWhenInterruptedCompareAndPutLeavesUnexpectedValue() throws Exception {
    valueToWriteBeforeInterruption = AN_OBJECT_ID_3;

    assertThrows(
        GlobalRefDbSystemError.class,
        () -> refDb.compareAndPut(A_TEST_PROJECT_NAME_KEY, oldRef, newValue));

    assertThat(readStoredValue()).isEqualTo(AN_OBJECT_ID_3);
  }

  private ObjectId readStoredValue() throws Exception {
    return ZkSharedRefDatabase.readObjectId(client.getData().forPath(path));
  }

  private class InterruptedSetDataZooKeeper extends ZooKeeper {
    InterruptedSetDataZooKeeper(
        String connectString, int sessionTimeout, Watcher watcher, boolean canBeReadOnly)
        throws IOException {
      super(connectString, sessionTimeout, watcher, canBeReadOnly);
    }

    @Override
    public Stat setData(String path, byte[] data, int version)
        throws KeeperException, InterruptedException {
      if (valueToWriteBeforeInterruption != null) {
        super.setData(
            path, ZkSharedRefDatabase.writeObjectId(valueToWriteBeforeInterruption), version);
      }
      throw new InterruptedException();
    }
  }
}
