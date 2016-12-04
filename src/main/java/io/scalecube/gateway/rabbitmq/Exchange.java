package io.scalecube.gateway.rabbitmq;

import java.util.Map;

public class Exchange {



  private String name;
  private String type;
  private boolean durable = true;
  private boolean autoDelete = false;
  private boolean internal = false;
  private Map<String, Object> properties = null;

  /**
   * Declare an exchange, via an interface that allows the complete set of arguments.
   * 
   * @see com.rabbitmq.client.AMQP.Exchange.Declare
   * @see com.rabbitmq.client.AMQP.Exchange.DeclareOk
   * @param exchange the name of the exchange
   * @param type the exchange type
   * @param durable true if we are declaring a durable exchange (the exchange will survive a server restart)
   * @param autoDelete true if the server should delete the exchange when it is no longer in use
   * @param internal true if the exchange is internal, i.e. can't be directly published to by a client.
   * @param arguments other properties (construction arguments) for the exchange
   * @return a declaration-confirm method to indicate the exchange was successfully declared
   * @throws java.io.IOException if an error is encountered
   */
  public Exchange(String name, String type, boolean durable, boolean autoDelete, boolean internal,
      Map<String, Object> properties) {
    this.name = name;
    this.type = type;
    this.durable = durable;
    this.autoDelete = autoDelete;
    this.internal = internal;
    this.properties = properties;
  }

  public String name() {
    return name;
  }

  // durable true if we are declaring a durable queue (the queue will survive a server restart)
  public boolean durable() {
    return durable;
  }

  // autoDelete true if we are declaring an autodelete queue (server will delete it when no longer in use)
  public boolean autoDelete() {
    return autoDelete;
  }

  public boolean internal() {
    return internal;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String name;
    private String type;
    private boolean durable;
    private boolean autoDelete;
    private boolean internal;
    private Map<String, Object> properties;

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder durable(boolean durable) {
      this.durable = durable;
      return this;
    }

    public Builder autoDelete(boolean autoDelete) {
      this.autoDelete = autoDelete;
      return this;
    }

    public Builder internal(boolean internal) {
      this.internal = internal;
      return this;
    }

    public Builder properties(Map<String, Object> properties) {
      this.properties = properties;
      return this;
    }

    public Builder type(String type) {
      this.type = type;
      return this;
    }

    public Exchange build() {
      return new Exchange(this.name,
          this.type,
          this.durable,
          this.autoDelete,
          this.internal,
          this.properties);
    }

    public Exchange create() {
      return build();
    }
  }

  public Map<String,Object> properties() {
    return properties;
  }

  public String type() {
    return this.type;
  }

}
