package io.scalecube.gateway.rabbitmq;

public interface MessageSerialization {
  
  public Object deserialize(byte[] data) throws Exception ;

  public static MessageSerialization empty() {
    
    return new MessageSerialization(){
      @Override
      public Object deserialize(byte[] data) throws Exception {
        return data;
      }

      @Override
      public byte[] serialize(Object obj) {
        if(obj instanceof byte[]){
          return (byte[]) obj;
        }else {
          throw new UnsupportedOperationException("Empty serialization accept only byte[] type");
        }
      }
    };
  }

  public byte[] serialize(Object obj);
  
}
