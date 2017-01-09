package io.scalecube.gateway.rabbitmq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.rabbitmq.client.MessageProperties;

import org.junit.Test;

public class TopicTest {

  @Test
  public void test_topic() {
    Topic topic = Topic.builder()
        .durable(false)
        .exchange("exchange")
        .autoDelete(false)
        .exclusive(false)
        .name("name")
        .properties(MessageProperties.PERSISTENT_TEXT_PLAIN)
        .build();
        
    assertTrue(!topic.durable());
    assertTrue(!topic.autoDelete());
    assertTrue(!topic.exclusive());
    assertEquals(topic.exchange(), "exchange");
    assertEquals(topic.name(), "name");
    assertEquals(topic.properties(), MessageProperties.PERSISTENT_TEXT_PLAIN);
    
    
  }
}
