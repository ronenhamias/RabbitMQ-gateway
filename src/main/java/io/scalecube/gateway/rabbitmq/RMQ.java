package io.scalecube.gateway.rabbitmq;

import com.rabbitmq.client.MessageProperties;

import rx.Observable;

public class RMQ {

  private RabbitListener listener;
  private MessageSerialization serialization = MessageSerialization.empty();
  
  public static class Builder {

    private String host = "localhost";
    private RabbitListener rabbitListener;
      
    public Builder host(String host) {
      this.host = host;
      return this;
    }
    
    public RMQ build() throws Exception {
      return new RMQ(new RabbitListener(this.host));
    }
  }

  private RMQ(RabbitListener rabbitListener) {
    this.listener = rabbitListener;
  }

  public static Builder builder() {
    return new Builder();
  }

  public RMQ queue(String queueName) throws Exception {
    listener.subscribe(queueName);
    return this;
  }

  public Observable<Object> listen() {
    return listener.listen(this.serialization);
  }

  public void publish(String queueName, Object obj) throws Exception{
    listener.channel().
      basicPublish( "", queueName,
            MessageProperties.PERSISTENT_TEXT_PLAIN,
            serialization.serialize(obj));
  }

  public RMQ plain() {
    this.serialization = new PlainMessageSeriazliation();
    return this;
  }
}
