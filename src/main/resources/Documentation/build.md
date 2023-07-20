# Build

This plugin is built with Bazel in-tree build.

## Build in Gerrit tree

Clone or link zookeeper plugin to the plugins directory of Gerrit's
source tree. Put the external dependency Bazel build file into the
Gerrit /plugins directory, replacing the existing empty one.

```
  cd gerrit/plugins
  ln -sf @PLUGIN@/external_plugin_deps.bzl .
```

From the Gerrit source tree issue the command:

```
  bazel build plugins/@PLUGIN@
```

The output is created in

```
  bazel-bin/plugins/@PLUGIN@/@PLUGIN@.jar
```

## Eclipse project setup

This project can be imported into the Eclipse IDE:

- Add the plugin name to the `CUSTOM_PLUGINS_TEST_DEPS`
set in Gerrit core in `tools/bzl/plugins.bzl`,
- execute:

```
  ./tools/eclipse/project.py
```

## Run tests

To execute the tests run

```
  bazel test //plugins/zookeeper-refdb/...
```

### Run tests on MacOS

Install socat

```
brew install socat
```

use socat to allow accessing the docker daemon via TCP

```
socat -d -v -d TCP-LISTEN:2375,fork UNIX-CONNECT:/var/run/docker.sock
```

To execute tests run

```
bazelisk test --test_env='DOCKER_HOST=tcp://127.0.0.1:2375' //plugins/zookeeper-refdb/...
```

### Debugging tests

```
  bazel test --test_output=streamed //plugins/zookeeper-refdb/...
```

If necessary increase log levels in `src/test/resources/log4j.properties`
to trace testcontainers and docker java API.

[Back to @PLUGIN@ documentation index][index]

[index]: index.html
