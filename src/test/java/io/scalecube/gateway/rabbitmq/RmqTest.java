package io.scalecube.gateway.rabbitmq;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.HashMap;

public class RmqTest {

  @Test
  public void test_rmq () {
    
    try {
      Rmq rmq = Rmq.builder()
        .port(-1)
        .host("localhost")
        .credentials(null)
        .timeout(1000)
        .build();
      
      Exchange exchange = Exchange.builder()
          .durable(false)
          .autoDelete(false)
          .internal(false)
          .type("direct")
          .name("in")
          .properties(new HashMap<>()).build();
      
      rmq.listen().subscribe(onNext->{
        System.out.println(onNext);
      });
      
      rmq.publish(exchange, "a" ,new String("a").getBytes());
      
      rmq.close();
      
      assertTrue(true);
    } catch (Exception e) {
    }
  }
}
