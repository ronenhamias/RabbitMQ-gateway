package io.scalecube.gateway.rabbitmq.serialization.text;

import io.scalecube.gateway.rabbitmq.MessageSerialization;

public class PlainMessageSeriazliation implements MessageSerialization {


  @Override
  public <T> T deserialize(byte[] data, Class<T> clazz) throws Exception {
    return (T) new String(data, "UTF-8");
  }

  @Override
  public <T> byte[] serialize(T value, Class<T> clazz) throws Exception {
    if (value instanceof String) {
      return value.toString().getBytes();
    } else {
      throw new UnsupportedOperationException("Plain text serialization accept only String type");
    }
  }

}
