load("//tools/bzl:maven_jar.bzl", "maven_jar")

def external_plugin_deps():
    CURATOR_VER = "5.5.0"

    maven_jar(
        name = "curator-test",
        artifact = "org.apache.curator:curator-test:" + CURATOR_VER,
        sha1 = "065586d87700aa29855e6aa566b210eadd1bb38d",
    )

    maven_jar(
        name = "curator-framework",
        artifact = "org.apache.curator:curator-framework:" + CURATOR_VER,
        sha1 = "b706a216e49352103bd2527e83b1ec2410924494",
    )

    maven_jar(
        name = "curator-recipes",
        artifact = "org.apache.curator:curator-recipes:" + CURATOR_VER,
        sha1 = "4aa0cfb129c36cd91528fc1b8775705280e60285",
    )

    maven_jar(
        name = "curator-client",
        artifact = "org.apache.curator:curator-client:" + CURATOR_VER,
        sha1 = "db2d83bdc0bac7b4f25fc113d8ce3eedc0a4e89c",
    )

    ZOOKEEPER_VER = "3.8.2"

    maven_jar(
        name = "zookeeper",
        artifact = "org.apache.zookeeper:zookeeper:" + ZOOKEEPER_VER,
        sha1 = "963e953f8e362fc3f253832876be2ae2dcde58d7",
    )

    maven_jar(
        name = "zookeeper-jute",
        artifact = "org.apache.zookeeper:zookeeper-jute:" + ZOOKEEPER_VER,
        sha1 = "f644829e30004ff4a079c357c4bb34bf5aa5fb94",
    )

    NETTY_VER = "4.1.94.Final"

    maven_jar(
        name = "netty-buffer",
        artifact = "io.netty:netty-buffer:" + NETTY_VER,
        attach_source = False,
        sha1 = "eec248b26f16e888688e5bb37b7eeda76b78d2f7",
    )

    maven_jar(
        name = "netty-codec",
        artifact = "io.netty:netty-codec:" + NETTY_VER,
        attach_source = False,
        sha1 = "c70ef20ca338558147887df60f46341bc47f6900",
    )

    maven_jar(
        name = "netty-common",
        artifact = "io.netty:netty-common:" + NETTY_VER,
        attach_source = False,
        sha1 = "ad4ecf779ebc794cd351f57792f56ea01387b868",
    )

    maven_jar(
        name = "netty-handler",
        artifact = "io.netty:netty-handler:" + NETTY_VER,
        attach_source = False,
        sha1 = "cd9121ce24d6d3f2898946d04b0ef3ec548b00b4",
    )

    maven_jar(
        name = "netty-resolver",
        artifact = "io.netty:netty-resolver:" + NETTY_VER,
        attach_source = False,
        sha1 = "e96f649e8e9dcb29a1f8e95328b99c9eb6cf76c2",
    )

    maven_jar(
        name = "netty-transport",
        artifact = "io.netty:netty-transport:" + NETTY_VER,
        attach_source = False,
        sha1 = "ec783a737f96991a87b1d5794e2f9eb2024d708a",
    )

    maven_jar(
        name = "netty-transport-classes-epoll",
        artifact = "io.netty:netty-transport-classes-epoll:" + NETTY_VER,
        attach_source = False,
        sha1 = "240e36cd5c2ffaf655913f8857f2d58b26394679",
    )

    maven_jar(
        name = "netty-transport-native-epoll",
        artifact = "io.netty:netty-transport-native-epoll:" + NETTY_VER,
        attach_source = False,
        sha1 = "1f77c54f1877d9ed51f207eb64a5634bdcc7efa1",
    )

    maven_jar(
        name = "netty-transport-native-unix-common",
        artifact = "io.netty:netty-transport-native-unix-common:" + NETTY_VER,
        attach_source = False,
        sha1 = "3fa5f9d04b6b782d869d6e0657d896eeadca5866",
    )

    JACKSON_VER = "2.15.2"

    maven_jar(
        name = "jackson-annotations",
        artifact = "com.fasterxml.jackson.core:jackson-annotations:" + JACKSON_VER,
        sha1 = "4724a65ac8e8d156a24898d50fd5dbd3642870b8",
    )

    maven_jar(
        name = "jackson-core",
        artifact = "com.fasterxml.jackson.core:jackson-core:" + JACKSON_VER,
        sha1 = "a6fe1836469a69b3ff66037c324d75fc66ef137c",
    )

    maven_jar(
        name = "jackson-dataformat-cbor",
        artifact = "com.fasterxml.jackson.dataformat:jackson-dataformat-cbor:" + JACKSON_VER,
        sha1 = "baafc85c70765594add14bd93f3efd68e1945b76",
    )

    maven_jar(
        name = "jackson-databind",
        artifact = "com.fasterxml.jackson.core:jackson-databind:" + JACKSON_VER,
        sha1 = "9353b021f10c307c00328f52090de2bdb4b6ff9c",
    )

    TESTCONTAINERS_VERSION = "1.18.3"

    maven_jar(
        name = "testcontainers",
        artifact = "org.testcontainers:testcontainers:" + TESTCONTAINERS_VERSION,
        sha1 = "a82f6258f92d50d278b9c67bdf5eabcaa5c08654",
    )

    maven_jar(
        name = "testcontainer-localstack",
        artifact = "org.testcontainers:localstack:" + TESTCONTAINERS_VERSION,
        sha1 = "2b7a8d4522330217545c4234b916b6b77f5c6f95",
    )

    DOCKER_JAVA_VERS = "3.3.2"

    maven_jar(
        name = "docker-java-api",
        artifact = "com.github.docker-java:docker-java-api:" + DOCKER_JAVA_VERS,
        sha1 = "0de6345d2f69638a224f73d9e62de83c7692e436",
    )

    maven_jar(
        name = "docker-java-transport",
        artifact = "com.github.docker-java:docker-java-transport:" + DOCKER_JAVA_VERS,
        sha1 = "a4c2cba248ccfefe9c5c8d8d4726f3e0b2b51104",
    )

    maven_jar(
        name = "docker-java-transport-zerodep",
        artifact = "com.github.docker-java:docker-java-transport-zerodep:" + DOCKER_JAVA_VERS,
        sha1 = "36ef508e5e48613afb7fafbf7e89184243738e96",
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
        artifact = "net.java.dev.jna:jna:5.13.0",
        sha1 = "1200e7ebeedbe0d10062093f32925a912020e747",
    )
