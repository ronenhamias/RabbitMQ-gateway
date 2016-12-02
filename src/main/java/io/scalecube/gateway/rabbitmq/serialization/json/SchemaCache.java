package io.scalecube.gateway.rabbitmq.serialization.json;

import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import java.util.concurrent.ConcurrentHashMap;

public class SchemaCache {

  static final ConcurrentHashMap<Class, Schema> schemaCache = new ConcurrentHashMap<>();
  
  public static <T> Schema<T> getOrCreate(Class<T> clazz) {
    
    schemaCache.computeIfAbsent(clazz, item -> compute(clazz));
    return schemaCache.get(clazz);
  }

  private static <T> Schema<T> compute(Class<T> clazz) {
    return RuntimeSchema.createFrom(clazz).getSchema(clazz); 
  }
  
}
