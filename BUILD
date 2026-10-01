load(
    "@com_googlesource_gerrit_bazlets//:gerrit_plugin.bzl",
    "gerrit_plugin",
    "gerrit_plugin_dependency_tests",
    "gerrit_plugin_tests",
)
load("@rules_java//java:defs.bzl", "java_library")

PLUGIN_DEPS = [
    ":global-refdb-neverlink",
    "@zookeeper-refdb_plugin_deps//:io_netty_netty_buffer",
    "@zookeeper-refdb_plugin_deps//:io_netty_netty_codec",
    "@zookeeper-refdb_plugin_deps//:io_netty_netty_common",
    "@zookeeper-refdb_plugin_deps//:io_netty_netty_handler",
    "@zookeeper-refdb_plugin_deps//:io_netty_netty_resolver",
    "@zookeeper-refdb_plugin_deps//:io_netty_netty_transport",
    "@zookeeper-refdb_plugin_deps//:io_netty_netty_transport_classes_epoll",
    "@zookeeper-refdb_plugin_deps//:io_netty_netty_transport_native_epoll",
    "@zookeeper-refdb_plugin_deps//:io_netty_netty_transport_native_unix_common",
    "@zookeeper-refdb_plugin_deps//:org_apache_curator_curator_client",
    "@zookeeper-refdb_plugin_deps//:org_apache_curator_curator_framework",
    "@zookeeper-refdb_plugin_deps//:org_apache_curator_curator_recipes",
    "@zookeeper-refdb_plugin_deps//:org_apache_zookeeper_zookeeper",
    "@zookeeper-refdb_plugin_deps//:org_apache_zookeeper_zookeeper_jute",
]

gerrit_plugin(
    name = "zookeeper-refdb",
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-PluginName: zookeeper-refdb",
        "Gerrit-Module: com.gerritforge.gerrit.plugins.validation.dfsrefdb.zookeeper.ZkValidationModule",
        "Gerrit-HttpModule: com.gerritforge.gerrit.plugins.bsl.HttpModule",
        "Gerrit-InitStep: com.gerritforge.gerrit.plugins.validation.dfsrefdb.zookeeper.ZkInit",
        "Implementation-Title: zookeeper ref-db plugin",
        "Implementation-URL: https://review.gerrithub.io/admin/repos/GerritForge/plugins_zookeeper",
    ],
    resources = glob(["src/main/resources/**/*"]),
    deps = PLUGIN_DEPS + [
        "//plugins/gerrit-bsl-license",
    ],
)

gerrit_plugin_tests(
    name = "zookeeper-refdb_tests",
    srcs = glob(["src/test/java/**/*.java"]),
    resources = glob(["src/test/resources/**/*"]),
    tags = [
        "local",
        "zookeeper",
    ],
    deps = [
        ":zookeeper-refdb__plugin_test_deps",
        "@audience-annotations//jar",
        "@snappy-java//jar",
        "@zookeeper-jute//jar",
        "@zookeeper//jar",
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
    exports = PLUGIN_DEPS + [
        ":zookeeper-refdb__plugin",
        "//plugins:plugin-lib-neverlink",
        "//plugins/global-refdb",
        "@zookeeper-refdb_plugin_deps//:com_fasterxml_jackson_core_jackson_annotations",
        "@zookeeper-refdb_plugin_deps//:com_fasterxml_jackson_core_jackson_databind",
        "@zookeeper-refdb_plugin_deps//:com_fasterxml_jackson_dataformat_jackson_dataformat_cbor",
        "@zookeeper-refdb_plugin_deps//:com_github_docker_java_docker_java_api",
        "@zookeeper-refdb_plugin_deps//:com_github_docker_java_docker_java_transport",
        "@zookeeper-refdb_plugin_deps//:com_github_docker_java_docker_java_transport_zerodep",
        "@zookeeper-refdb_plugin_deps//:net_java_dev_jna_jna",
        "@zookeeper-refdb_plugin_deps//:org_apache_curator_curator_test",
        "@zookeeper-refdb_plugin_deps//:org_rnorth_duct_tape_duct_tape",
        "@zookeeper-refdb_plugin_deps//:org_rnorth_visible_assertions_visible_assertions",
        "@zookeeper-refdb_plugin_deps//:org_testcontainers_localstack",
        "@zookeeper-refdb_plugin_deps//:org_testcontainers_testcontainers",
    ],
)

gerrit_plugin_dependency_tests(plugin = "zookeeper-refdb")
