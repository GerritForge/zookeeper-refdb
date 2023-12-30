load("//tools/bzl:junit.bzl", "junit_tests")
load(
    "//tools/bzl:plugin.bzl",
    "PLUGIN_DEPS",
    "PLUGIN_TEST_DEPS",
    "gerrit_plugin",
)

gerrit_plugin(
    name = "zookeeper-refdb",
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-PluginName: zookeeper-refdb",
        "Gerrit-Module: com.googlesource.gerrit.plugins.validation.dfsrefdb.zookeeper.ZkValidationModule",
        "Gerrit-InitStep: com.googlesource.gerrit.plugins.validation.dfsrefdb.zookeeper.ZkInit",
        "Implementation-Title: zookeeper ref-db plugin",
        "Implementation-URL: https://review.gerrithub.io/admin/repos/GerritForge/plugins_zookeeper",
    ],
    resources = glob(["src/main/resources/**/*"]),
    deps = [
        ":global-refdb-neverlink",
        "@curator-client//jar",
        "@curator-framework//jar",
        "@curator-recipes//jar",
        "@netty-buffer//jar",
        "@netty-codec//jar",
        "@netty-common//jar",
        "@netty-handler//jar",
        "@netty-resolver//jar",
        "@netty-transport-classes-epoll//jar",
        "@netty-transport-native-epoll//jar",
        "@netty-transport-native-unix-common//jar",
        "@netty-transport//jar",
        "@zookeeper-jute//jar",
        "@zookeeper//jar",
    ],
)

junit_tests(
    name = "zookeeper-refdb_tests",
    srcs = glob(["src/test/java/**/*.java"]),
    resources = glob(["src/test/resources/**/*"]),
    tags = [
        "local",
        "zookeeper",
    ],
    deps = [
        ":zookeeper-refdb__plugin_test_deps",
    ],
)

java_library(
    name = "global-refdb-neverlink",
    neverlink = 1,
    exports = ["//plugins/global-refdb"],
)

java_library(
    name = "zookeeper-refdb__plugin_test_deps",
    testonly = 1,
    visibility = ["//visibility:public"],
    exports = PLUGIN_DEPS + PLUGIN_TEST_DEPS + [
        ":zookeeper-refdb__plugin",
        "//plugins/global-refdb",
        "@curator-client//jar",
        "@curator-framework//jar",
        "@curator-recipes//jar",
        "@curator-test//jar",
        "@docker-java-api//jar",
        "@docker-java-transport-zerodep//jar",
        "@docker-java-transport//jar",
        "@duct-tape//jar",
        "@jackson-annotations//jar",
        "@jackson-databind//jar",
        "@jackson-dataformat-cbor//jar",
        "@jna//jar",
        "@testcontainer-localstack//jar",
        "@testcontainers//jar",
        "@visible-assertions//jar",
    ],
)
