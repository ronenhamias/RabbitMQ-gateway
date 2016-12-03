package io.scalecube.gateway.rabbitmq.serialization.proto;

import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.concurrent.ConcurrentHashMap;

public class SchemaCache {

  static final ConcurrentHashMap<Class, Schema> schemaCache = new ConcurrentHashMap<>();
  
  public static <T> Schema<T> getOrCreate(Class<T> clazz) {
    return schemaCache.computeIfAbsent(clazz, item -> compute(clazz));
  }

  private static <T> Schema<T> compute(Class<T> clazz) {
    return RuntimeSchema.createFrom(clazz).getSchema(clazz); 
  }
  
}
