# Gerrit Zookeeper ref-db

This plugin provides an implementation of the Gerrit global ref-db backed by
[Apache Zookeeper](https://zookeeper.apache.org/).

Requirements for using this plugin are:

- Gerrit v3.2 or later
- Apache Zookeeper v3.4 or later

## Typical use-case

The global ref-db is a typical use-case of a Gerrit multi-master scenario
in a multi-site setup. Refer to the
[Gerrit multi-site plugin](https://gerrit.googlesource.com/plugins/multi-site/+/master/DESIGN.md)
for more details on the high level architecture.

Build
---------------------
Gerrit Zookeeper ref-db plugin can be build as a regular 'in-tree' plugin. That means that is required to
clone a Gerrit source tree first and then to have the plugin source directory into
the /plugins path.

Additionally, the plugins/external_plugin_deps.bzl file needs to be
updated to match the zookeeper ref-db plugin one.

    git clone --recursive https://gerrit.googlesource.com/gerrit
    git clone https://gerrit.googlesource.com/plugins/zookeeper-refdb gerrit/plugins/zookeeper-refdb
    cd gerrit
    rm plugins/external_plugin_deps.bzl
    ln -s ./zookeeper-refdb/external_plugin_deps.bzl plugins/.

To build the Zookeeper ref-db plugin, issue the command from the Gerrit source path:

    bazel build plugins/zookeeper-refdb

The output is created in

    bazel-bin/plugins/zookeeper-refdb/zookeeper-refdb.jar

