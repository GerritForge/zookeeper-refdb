# Gerrit Zookeeper ref-db

This plugin provides an implementation of the Gerrit global ref-db backed by
[Apache Zookeeper](https://zookeeper.apache.org/).

Requirements for using this plugin are:

- Gerrit v3.0 or later
- Apache Zookeeper v3.4 or later

## Typical use-case

The global ref-db is a typical use-case of a Gerrit multi-master scenario
in a multi-site setup. Refer to the
[Gerrit multi-site plugin](https://gerrit.googlesource.com/plugins/multi-site/+/stable-3.1/DESIGN.md)
for more details on the high level architecture.
