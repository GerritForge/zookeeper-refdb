# Gerrit Zookeeper ref-db

This plugin provides an implementation of the Gerrit global ref-db backed by
[Apache Zookeeper](https://zookeeper.apache.org/).

Requirements for using this plugin are:

- Gerrit v3.2 or later
- Apache Zookeeper v3.4 or later

## Typical use-case

The global ref-db is a typical use-case of a Gerrit multi-master scenario
in a multi-site setup. Refer to the
[Gerrit multi-site plugin](https://github.com/GerritForge/multi-site/blob/master/DESIGN.md)
for more details on the high level architecture.
