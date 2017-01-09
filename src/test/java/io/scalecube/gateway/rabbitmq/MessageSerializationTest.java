package io.scalecube.gateway.rabbitmq;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MessageSerializationTest {

  @Test
  public void test_message_serialization() {
    
    MessageSerialization empty = MessageSerialization.empty();
    
    try {
      byte[] result = (byte[]) empty.deserialize(new String("a").getBytes(), Object.class);
      assertEquals(result[0],97);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    
    try {
      empty.serialize(new String("a"), Object.class);
    } catch (Exception e) {
      assertEquals(e.getMessage(),"Empty serialization accept only byte[] type");
    }
  }
}
