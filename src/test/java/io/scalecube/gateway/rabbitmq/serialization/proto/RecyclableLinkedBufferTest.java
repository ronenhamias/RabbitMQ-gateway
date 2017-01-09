package io.scalecube.gateway.rabbitmq.serialization.proto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import io.scalecube.gateway.rabbitmq.serialization.proto.RecyclableLinkedBuffer;

public class RecyclableLinkedBufferTest {

  @Test
  public void test_recyclable_linked_buffer(){
    
    RecyclableLinkedBuffer rlb = new RecyclableLinkedBuffer();
    
    try {
      rlb.buffer();
    } catch (Exception ex ){
      assertEquals(ex.getMessage() , "Call LinkedBufferWrapper.get() first");
    }
    
    try {
      rlb.release();
    } catch (Exception ex ){
      assertEquals(ex.getMessage() , "Call LinkedBufferWrapper.get() first");
    }
    
  }
}
