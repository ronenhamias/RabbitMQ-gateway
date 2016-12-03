package io.scalecube.gateway.rabbitmq;

import static org.junit.Assert.assertTrue;

import io.scalecube.examples.services.GreetingRequest;
import io.scalecube.gateway.rabbitmq.serialization.proto.JsonMessageSerialization;

import org.junit.Test;

public class JsonMessageSerializationTest {

  @Test
  public void test_message_serialization() throws Exception{
    JsonMessageSerialization serialization = new JsonMessageSerialization();
    
    byte[] bytes = serialization.serialize(new GreetingRequest("hello"),GreetingRequest.class);
    
    assertTrue(bytes!=null);
    
    GreetingRequest request = serialization.deserialize(bytes, GreetingRequest.class);
    
    assertTrue(request.getName().equals("hello"));
  }
}
