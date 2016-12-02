package io.scalecube.gateway.rabbitmq;

public interface MessageSerialization {

  public <T> T deserialize(byte[] data, Class<T> clazz) throws Exception;

  public <T> byte[] serialize(Object value) throws Exception;

  public <T> byte[] serialize(T value, Class<T> clazz) throws Exception;
  
  public static MessageSerialization empty() {
    return new MessageSerialization() {
      
      @Override
      public <T> T deserialize(byte[] data, Class<T> clazz) throws Exception {
        return (T) data;
      }
      
      @Override
      public <T> byte[] serialize(Object value) throws Exception {
        if (value instanceof byte[]) {
          return (byte[]) value;
        } else {
          throw new UnsupportedOperationException("Empty serialization accept only byte[] type");
        }
      }

      @Override
      public <T> byte[] serialize(T value, Class<T> clazz) throws Exception {
        if (value instanceof byte[]) {
          return (byte[]) value;
        } else {
          throw new UnsupportedOperationException("Empty serialization accept only byte[] type");
        }
      }
    };
  }
}
