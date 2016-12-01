package io.scalecube.gateway.rabbitmq;

public interface MessageSerialization {

  public <T> Object deserialize(byte[] data, Class<T> clazz) throws Exception;

  public <T> byte[] serialize(T value) throws Exception;

  public static MessageSerialization empty() {
    return new MessageSerialization() {
      
      @Override
      public <T> Object deserialize(byte[] data, Class<T> clazz) throws Exception {
        return (T) data;
      }
      
      @Override
      public byte[] serialize(Object obj) {
        if (obj instanceof byte[]) {
          return (byte[]) obj;
        } else {
          throw new UnsupportedOperationException("Empty serialization accept only byte[] type");
        }
      }
      
    };
  }
}
