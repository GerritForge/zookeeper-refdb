// Copyright (C) 2025 GerritForge, Inc.
//
// Licensed under the BSL 1.1 (the "License");
// you may not use this file except in compliance with the License.
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.gerritforge.gerrit.plugins.validation.dfsrefdb.zookeeper;

import com.google.gerrit.extensions.registration.DynamicSet;
import com.google.inject.Inject;

public class StringDeserializerFactory {

  private final DynamicSet<StringDeserializer> stringToGenericDeserializers;

  @Inject
  public StringDeserializerFactory(DynamicSet<StringDeserializer> stringToGenericDeserializers) {
    this.stringToGenericDeserializers = stringToGenericDeserializers;
  }

  @SuppressWarnings("unchecked")
  public <T> StringDeserializer<T> create(final Class<T> clazz) throws DeserializerException {
    for (StringDeserializer<?> stringDeserializer : stringToGenericDeserializers) {
      if (stringDeserializer.getTypeClass().getName().equals(clazz.getTypeName())) {
        return (StringDeserializer<T>) stringDeserializer;
      }
    }
    throw new DeserializerException("No serializer registered for class " + clazz.getName());
  }
}
