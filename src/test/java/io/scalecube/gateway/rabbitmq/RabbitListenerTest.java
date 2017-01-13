package io.scalecube.gateway.rabbitmq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.net.SocketTimeoutException;

public class RabbitListenerTest {

  @Test
  public void test_rabbit_listener() {
    try {
      Rmq.Builder builder = Rmq.builder()
          .host("localhost")
          .port(5672)
          .timeout(3)
          .autoRecovery(false)
          .networkRecoveryInterval(1000)
          .serialization(MessageSerialization.empty());

      new RabbitListener(builder);
    } catch (Throwable e) {
      assertTrue(e instanceof SocketTimeoutException);
    }

    try {
      Rmq.Builder builder = Rmq.builder()
          .host("localhost")
          .port(-1)
          .timeout(3)
          .credentials(null)
          .autoRecovery(false)
          .networkRecoveryInterval(1000)
          
          .serialization(MessageSerialization.empty());

      new RabbitListener(builder);
    } catch (Exception e) {
      assertEquals(e.getMessage().toString(), "connect timed out");
    }

    try {
      Rmq.Builder builder = Rmq.builder()
          .host("localhost")
          .port(-1)
          .timeout(1000)
          .credentials(new BasicCredentials("a", "b"))
          .autoRecovery(false)
          .networkRecoveryInterval(1000)
          .serialization(MessageSerialization.empty());

      new RabbitListener(builder);
    } catch (Exception e) {
      assertEquals(e.getMessage().toString(),
          "ACCESS_REFUSED - Login was refused using authentication mechanism PLAIN. For details see the broker logfile.");
    }

    try {
      Rmq.Builder builder = Rmq.builder()
          .host("localhost")
          .port(-1)
          .timeout(1000)
          .credentials(new Credentials() {})
          .autoRecovery(false)
          .networkRecoveryInterval(1000)
          .serialization(MessageSerialization.empty());

      new RabbitListener(builder);

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
          .autoRecovery(false)
          .networkRecoveryInterval(1000)
          .serialization(MessageSerialization.empty());

      
      RabbitListener listener = new RabbitListener(builder);

      assertTrue(builder.networkRecoveryInterval()== 1000);
      assertTrue(builder.autoRecovery() == false);
      assertTrue(listener.listen() != null);
      assertTrue(listener.channel().isOpen());
      listener.close();
      assertTrue(!listener.channel().isOpen());
    } catch (Exception ex) {
      assertEquals(ex.getMessage().toString(), ".");
    }

  }

}
