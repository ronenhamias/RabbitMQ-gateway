package io.scalecube.gateway.rabbitmq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
      Credentials cred = new Credentials() {};
      RabbitPublisher publisher = new RabbitPublisher("localhost", -1, 1000, cred);

    } catch (Exception e) {
      assertEquals(e.getMessage().toString(),
          "ACCESS_REFUSED - Login was refused using authentication mechanism PLAIN. For details see the broker logfile.");
    }

    try {
      RabbitPublisher publisher = new RabbitPublisher("localhost", -1, 1000, null);
      publisher.subscribe(Exchange.builder().name("in").build());
      assertTrue(publisher.channel().isOpen());
      publisher.close();
      assertTrue(!publisher.channel().isOpen());
    } catch (Exception ex) {
      assertEquals(ex.getMessage().toString(), ".");
    }

  }

}
