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
bazel test --test_tag_filters=zookeeper //...
```
or
```
bazel test //plugins/@PLUGIN@/...
```

### Debugging tests

```
bazel test --test_output=streamed //plugins/@PLUGIN@/...
```

If necessary increase log levels in `src/test/resources/log4j.properties`
to trace testcontainers and docker java API.

### Tracing traffic to docker daemon

If you face issue you can trace traffic to the docker daemon using
[socat](https://linux.die.net/man/1/socat) exposing the docker daemon via TCP.

Run socat to log diagnostics and show the traffic to the docker daemon

```
socat -dd -v TCP-LISTEN:2375,fork UNIX-CONNECT:/var/run/docker.sock
```

Execute the tests over TCP

```
bazelisk test --test_env='DOCKER_HOST=tcp://127.0.0.1:2375' //plugins/@PLUGIN@/...
```

[Back to @PLUGIN@ documentation index][index]

[index]: index.html
