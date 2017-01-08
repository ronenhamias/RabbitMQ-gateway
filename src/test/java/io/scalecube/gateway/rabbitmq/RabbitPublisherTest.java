package io.scalecube.gateway.rabbitmq;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RabbitPublisherTest {

  @Test
  public void test_rabbit_publisher() {
    try {
      RabbitPublisher publisher = new RabbitPublisher("localhost", 1, 1000, null);
    } catch (Exception e) {
      assertEquals(e.getMessage().toString(), "connect timed out");
    }
  }

}
