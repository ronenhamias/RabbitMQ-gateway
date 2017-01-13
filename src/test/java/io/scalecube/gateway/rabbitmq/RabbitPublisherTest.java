package io.scalecube.gateway.rabbitmq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.HashMap;

public class RabbitPublisherTest {

  @Test
  public void test_rabbit_publisher() {

    try {
      Rmq.Builder builder = Rmq.builder()
          .host("localhost")
          .port(5672)
          .timeout(5)
          .serialization(MessageSerialization.empty());

      new RabbitPublisher(builder);
    } catch (Exception e) {
      if (e.getMessage() != null) {
        assertEquals(e.getMessage().toString(), "connect timed out");
      } else {
        e.printStackTrace();
      }
    }

    try {
      Rmq.Builder builder = Rmq.builder()
          .host("localhost")
          .port(-1)
          .timeout(3)
          .serialization(null);

      new RabbitPublisher(builder);

    } catch (Exception e) {
      assertEquals(e.getMessage().toString(), "connect timed out");
    }

    try {
      Rmq.Builder builder = Rmq.builder()
          .host("localhost")
          .port(-1)
          .timeout(1000)
          .credentials(new BasicCredentials("a", "b"));

      new RabbitPublisher(builder);

    } catch (Exception e) {
      assertEquals(e.getMessage().toString(),
          "ACCESS_REFUSED - Login was refused using authentication mechanism PLAIN. For details see the broker logfile.");
    }

    try {
      Rmq.Builder builder = Rmq.builder()
          .host("localhost")
          .port(-1)
          .timeout(1000)
          .credentials(new Credentials() {});

      new RabbitPublisher(builder);

    } catch (Exception e) {
      assertEquals(e.getMessage().toString(),
          "ACCESS_REFUSED - Login was refused using authentication mechanism PLAIN. For details see the broker logfile.");
    }

    try {
      Rmq.Builder builder = Rmq.builder()
          .host("localhost")
          .port(-1)
          .timeout(1000)
          .credentials(null)
          .networkRecoveryInterval(2000);

      assertTrue(!builder.autoRecovery());
      assertEquals(builder.networkRecoveryInterval(), 2000);

      RabbitPublisher publisher = new RabbitPublisher(builder);
      Exchange exchange = Exchange.builder()
          .durable(false)
          .autoDelete(false)
          .internal(false)
          .type("direct")
          .name("in")
          .properties(new HashMap<>()).build();
      assertTrue(!exchange.internal());

      publisher.subscribe(exchange);

      assertTrue(publisher.channel().isOpen());
      publisher.close();
      assertTrue(!publisher.channel().isOpen());
    } catch (Exception ex) {
      assertEquals(ex.getMessage().toString(), ".");
    }

  }

}
