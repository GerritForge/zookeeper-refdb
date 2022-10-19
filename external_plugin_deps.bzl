load("//tools/bzl:maven_jar.bzl", "maven_jar")

def external_plugin_deps():
    CURATOR_VER = "4.2.0"
    TESTCONTAINERS_VERSION = "1.15.3"
    DOCKER_JAVA_VERS = "3.2.8"

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
        artifact = "org.apache.zookeeper:zookeeper:3.5.8",
        sha1 = "fc0d02657ed5b26029daa50d7f98b9806a0b13af",
    )

    maven_jar(
        name = "zookeeper-jute_3.5",
        artifact = "org.apache.zookeeper:zookeeper-jute:3.5.8",
        sha1 = "b399078f6ccfd6c258e42054091052e8f3e05824",
    )

    maven_jar(
        name = "netty-all",
        artifact = "io.netty:netty-all:4.1.48.Final",
        sha1 = "ebb3666ba4883ba81920cec8ccb1a3adcc827eb1",
    )

    maven_jar(
        name = "zookeeper_3.4",
        artifact = "org.apache.zookeeper:zookeeper:3.4.14",
        sha1 = "c114c1e1c8172a7cd3f6ae39209a635f7a06c1a1",
    )

    maven_jar(
        name = "global-refdb",
<<<<<<< HEAD
        artifact = "com.gerritforge:global-refdb:3.6.0-rc3.1",
        sha1 = "9e28fdc65dcdecefd852f6c24dd7eec339a01870",
    )

    JACKSON_VER = "2.10.4"

    maven_jar(
        name = "jackson-annotations",
        artifact = "com.fasterxml.jackson.core:jackson-annotations:" + JACKSON_VER,
        sha1 = "6ae6028aff033f194c9710ad87c224ccaadeed6c",
    )

    maven_jar(
        name = "jackson-core",
        artifact = "com.fasterxml.jackson.core:jackson-core:" + JACKSON_VER,
        sha1 = "8796585e716440d6dd5128b30359932a9eb74d0d",
    )

    maven_jar(
        name = "jackson-dataformat-cbor",
        artifact = "com.fasterxml.jackson.dataformat:jackson-dataformat-cbor:" + JACKSON_VER,
        sha1 = "c854bb2d46138198cb5d4aae86ef6c04b8bc1e70",
    )

    maven_jar(
        name = "jackson-databind",
        artifact = "com.fasterxml.jackson.core:jackson-databind:" + JACKSON_VER,
        sha1 = "76e9152e93d4cf052f93a64596f633ba5b1c8ed9",
    )

    TESTCONTAINERS_VERSION = "1.15.3"

    maven_jar(
=======
        artifact = "com.gerritforge:global-refdb:3.4.0",
        sha1 = "a1c7b02ddabe0dd0a989fb30ca18b61fe95ee894",
    )

    maven_jar(
        name = "jackson-annotations",
        artifact = "com.fasterxml.jackson.core:jackson-annotations:2.10.3",
        sha1 = "0f63b3b1da563767d04d2e4d3fc1ae0cdeffebe7",
    )

    maven_jar(
        name = "testcontainers",
        artifact = "org.testcontainers:testcontainers:" + TESTCONTAINERS_VERSION,
        sha1 = "95c6cfde71c2209f0c29cb14e432471e0b111880",
    )

    DOCKER_JAVA_VERS = "3.2.8"

    maven_jar(
        name = "docker-java-api",
        artifact = "com.github.docker-java:docker-java-api:" + DOCKER_JAVA_VERS,
        sha1 = "4ac22a72d546a9f3523cd4b5fabffa77c4a6ec7c",
    )

    maven_jar(
        name = "docker-java-transport",
        artifact = "com.github.docker-java:docker-java-transport:" + DOCKER_JAVA_VERS,
        sha1 = "c3b5598c67d0a5e2e780bf48f520da26b9915eab",
    )

    maven_jar(
        name = "duct-tape",
        artifact = "org.rnorth.duct-tape:duct-tape:1.0.8",
        sha1 = "92edc22a9ab2f3e17c9bf700aaee377d50e8b530",
    )

    maven_jar(
        name = "visible-assertions",
        artifact = "org.rnorth.visible-assertions:visible-assertions:2.1.2",
        sha1 = "20d31a578030ec8e941888537267d3123c2ad1c1",
    )

    maven_jar(
        name = "jna",
        artifact = "net.java.dev.jna:jna:5.5.0",
        sha1 = "0e0845217c4907822403912ad6828d8e0b256208",
    )

    maven_jar(
        name = "testcontainer-localstack",
        artifact = "org.testcontainers:localstack:" + TESTCONTAINERS_VERSION,
        sha1 = "7aa69995bdaafb4b06e69fdab9bd98c4fddee43d",
    )
