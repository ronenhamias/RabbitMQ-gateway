package io.scalecube.gateway.rabbitmq;

public class PlainMessageSeriazliation implements MessageSerialization{

  @Override
  public Object deserialize(byte[] data) throws Exception {
    return new String(data, "UTF-8");
  }

  @Override
  public byte[] serialize(Object obj) {
    if(obj instanceof String){
      return obj.toString().getBytes();
    } else {
      throw new UnsupportedOperationException("Plain text serialization accept only String type");
    }
  }
}
