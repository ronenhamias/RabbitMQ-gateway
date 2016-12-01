package io.scalecube.gateway.rabbitmq;

import rx.Observable;

public class RMQ {

  private RabbitListener listener;
  private MessageSerialization serialization = MessageSerialization.empty();
  
  public static class Builder {

    private String host = "localhost";
    
    private RabbitListener rabbitListener;

    private int port = -1;

    private int timeout = 0;

    private String password = null;
      
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
    public void password(String password) {
        this.password = password;
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
          this.password));
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

  public <T> Observable<T> listen() {
    return listener.listen(this.serialization);
  }

  public void publish(Topic topic, Object obj) throws Exception{
    listener.channel().
      basicPublish( "", topic.name(),
            topic.properties(),
            serialization.serialize(obj));
  }

  public RMQ plain() {
    this.serialization = new PlainMessageSeriazliation();
    return this;
  }
}
