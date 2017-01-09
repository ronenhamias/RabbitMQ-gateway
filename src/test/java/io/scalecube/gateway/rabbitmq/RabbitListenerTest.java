package io.scalecube.gateway.rabbitmq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.net.SocketTimeoutException;

public class RabbitListenerTest {

  @Test
  public void test_rabbit_listener() {
    try {
      new RabbitListener("localhost", 5672, 3, null, MessageSerialization.empty());
    } catch (Throwable e) {
      e.printStackTrace();
      if(e instanceof SocketTimeoutException) {
        System.out.println(e.getStackTrace());
        assertEquals(e.getMessage().toString(), "connect timed out");
      }
    }

    try {
      new RabbitListener("localhost", -1, 3, null,MessageSerialization.empty());
    } catch (Exception e) {
      assertEquals(e.getMessage().toString(), "connect timed out");
    }

    try {
      new RabbitListener("localhost", -1, 1000, new BasicCredentials("a", "b"),MessageSerialization.empty());
    } catch (Exception e) {
      assertEquals(e.getMessage().toString(),
          "ACCESS_REFUSED - Login was refused using authentication mechanism PLAIN. For details see the broker logfile.");
    }

    try {
      Credentials cred = new Credentials() {};
      new RabbitListener("localhost", -1, 1000, cred,MessageSerialization.empty());

    } catch (Exception e) {
      assertEquals(e.getMessage().toString(),
          "ACCESS_REFUSED - Login was refused using authentication mechanism PLAIN. For details see the broker logfile.");
    }

    try {
      RabbitListener  listener =new RabbitListener("localhost", -1, 1000, null,MessageSerialization.empty());
      assertTrue(listener.listen() !=null);
      assertTrue(listener.channel().isOpen());
      listener.close();
      assertTrue(!listener.channel().isOpen());
    } catch (Exception ex) {
      assertEquals(ex.getMessage().toString(), ".");
    }

  }

}
