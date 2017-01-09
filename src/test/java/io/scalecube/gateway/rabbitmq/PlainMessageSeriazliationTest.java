package io.scalecube.gateway.rabbitmq;

import static org.junit.Assert.assertEquals;

import io.scalecube.gateway.rabbitmq.serialization.text.PlainMessageSeriazliation;

import org.junit.Test;

public class PlainMessageSeriazliationTest {

  @Test
  public void test_plain_message_serialization () {
    
    PlainMessageSeriazliation serialization = new PlainMessageSeriazliation();
    
    try {
      serialization.serialize(new Object(), Object.class);
    } catch (Exception e) {
      assertEquals(e.getMessage(),"Plain text serialization accept only String type");
    }
    
  }
}
