package io.scalecube.gateway.rabbitmq.serialization;

import io.scalecube.gateway.rabbitmq.MessageSerialization;

public class PlainMessageSeriazliation implements MessageSerialization{

  @Override
  public byte[] serialize(Object obj) {
    if(obj instanceof String){
      return obj.toString().getBytes();
    } else {
      throw new UnsupportedOperationException("Plain text serialization accept only String type");
    }
  }

  @Override
  public <T> T deserialize(byte[] data, Class<T> clazz) throws Exception {
    return (T) new String(data, "UTF-8");
  }

  @Override
  public <T> byte[] serialize(T value, Class<T> clazz) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }
  
}
