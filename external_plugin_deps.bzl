load("//tools/bzl:maven_jar.bzl", "maven_jar")

def external_plugin_deps():
    CURATOR_VER = "4.2.0"

    maven_jar(
        name = "curator-test",
        artifact = "org.apache.curator:curator-test:" + CURATOR_VER,
        sha1 = "98ac2dd69b8c07dcaab5e5473f93fdb9e320cd73",
    )

    maven_jar(
        name = "curator-framework",
        artifact = "org.apache.curator:curator-framework:" + CURATOR_VER,
        sha1 = "5b1cc87e17b8fe4219b057f6025662a693538861",
    )

    maven_jar(
        name = "curator-recipes",
        artifact = "org.apache.curator:curator-recipes:" + CURATOR_VER,
        sha1 = "7f775be5a7062c2477c51533b9d008f70411ba8e",
    )

    maven_jar(
        name = "curator-client",
        artifact = "org.apache.curator:curator-client:" + CURATOR_VER,
        sha1 = "d5d50930b8dd189f92c40258a6ba97675fea3e15",
    )

    maven_jar(
        name = "zookeeper_3.5",
        artifact = "org.apache.zookeeper:zookeeper:3.5.7",
        sha1 = "12bdf55ba8be7fc891996319d37f35eaad7e63ea",
    )

    maven_jar(
        name = "zookeeper-jute_3.5",
        artifact = "org.apache.zookeeper:zookeeper-jute:3.5.7",
        sha1 = "1270f80b08904499a6839a2ee1800da687ad96b4",
    )

    maven_jar(
        name = "netty-all_3.5",
        artifact = "io.netty:netty-all:4.1.45.Final",
        sha1 = "e830eae36d22f2bba3118a3bc08e17f15263a01d",
    )

    maven_jar(
        name = "zookeeper_3.4",
        artifact = "org.apache.zookeeper:zookeeper:3.4.14",
        sha1 = "c114c1e1c8172a7cd3f6ae39209a635f7a06c1a1",
    )

    maven_jar(
        name = "global-refdb",
        artifact = "com.gerritforge:global-refdb:3.3.0",
        sha1 = "8ef0600757b7468dc023c5030ce2cfeaa8ea0b64",
    )
