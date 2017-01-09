package io.scalecube.gateway.rabbitmq;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RabbitPublisherTest {

  @Test
  public void test_rabbit_publisher() {
    try {
      RabbitPublisher publisher = new RabbitPublisher("localhost", 5672, 1, null);
    } catch (Exception e) {
      assertEquals(e.getMessage().toString(), "connect timed out");
    }

    try {
      RabbitPublisher publisher = new RabbitPublisher("localhost", -1, 1, null);
    } catch (Exception e) {
      assertEquals(e.getMessage().toString(), "connect timed out");
    }

    try {
      RabbitPublisher publisher = new RabbitPublisher("localhost", -1, 1000, new BasicCredentials("a", "b"));
    } catch (Exception e) {
      assertEquals(e.getMessage().toString(),
          "ACCESS_REFUSED - Login was refused using authentication mechanism PLAIN. For details see the broker logfile.");
    }
    
    try {
      Credentials cred = new Credentials(){};
      RabbitPublisher publisher = new RabbitPublisher("localhost", -1, 1000, cred);
    } catch (Exception e) {
      assertEquals(e.getMessage().toString(),
          "ACCESS_REFUSED - Login was refused using authentication mechanism PLAIN. For details see the broker logfile.");
    }
    
  }

}
