package io.scalecube.gateway.rabbitmq;

import io.scalecube.gateway.rabbitmq.serialization.proto.JsonMessageSerialization;
import io.scalecube.gateway.rabbitmq.serialization.proto.ProtoMessageSerialization;
import io.scalecube.gateway.rabbitmq.serialization.text.PlainMessageSeriazliation;

import rx.Observable;

public class RMQ {

  private RabbitListener listener;
  private MessageSerialization serialization = MessageSerialization.empty();
  
  public static class Builder {

    private String host = "localhost";
    
    private RabbitListener rabbitListener;

    private int port = -1;

    private int timeout = 0;

    private Credentials credentials;
      
    /**
     * Set the host of the broker.
     * @param host to use when connecting to the RMQ broker.
     */
    public Builder host(String host) {
      this.host = host;
      return this;
    }
    
    /**
     * Set the port of the broker.
     * @param port to use when connecting to the RMQ broker
     */
    public Builder port(int port) {
      this.port = port;
      return this;
    }
    
    /**
     * Set the password.
     * @param password the password to use when connecting to the RMQ broker if null not in use.
     */
    public Builder credentials(Credentials credentials) {
        this.credentials = credentials;
        return this;
    }
      
    /**
     * Set the TCP connection timeout.
     * @param timeout connection TCP establishment timeout in milliseconds; zero for infinite
     */
    public Builder timeout(int timeout) {
      this.timeout = timeout;
      return this;
    }
    
    public RMQ build() throws Exception {
      return new RMQ(new RabbitListener(this.host,
          this.port,
          this.timeout,
          this.credentials));
    }
  }

  private RMQ(RabbitListener rabbitListener) {
    this.listener = rabbitListener;
  }

  public static Builder builder() {
    return new Builder();
  }

  public RMQ topic(Topic topic) throws Exception {
    listener.subscribe(topic);
    return this;
  }

  public RMQ exchange(Exchange exchange) throws Exception {
    listener.subscribe(exchange);
    return this;
  }
  
  public <T> Observable<T> listen(Class<T> class1) {
    return listener.listen(this.serialization,class1);
  }

  public Observable<byte[]> listen() {
    return listener.listen();
  }
  
  public <T> void publish(Topic topic, Object obj) throws Exception{
    listener.channel().
      basicPublish( "", topic.name(),
            topic.properties(),
            serialization.serialize((T)obj,(Class<T>)obj.getClass()));
  }

  public RMQ plain() {
    this.serialization = new PlainMessageSeriazliation();
    return this;
  }
  
  public RMQ proto() {
    this.serialization = new ProtoMessageSerialization();
    return this;
  }
  
  public RMQ json() {
    this.serialization = new JsonMessageSerialization();
    return this;
  }

 
}
