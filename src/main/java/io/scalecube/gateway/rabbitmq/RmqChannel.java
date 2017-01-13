package io.scalecube.gateway.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RmqChannel implements AutoCloseable {


  protected Connection connection;
  protected Channel channel;

  /**
   * initialize rabbit mq listener
   * 
   * @param builder properties to init channel.
   * @throws Exception if failed.
   */
  public RmqChannel(Rmq.Builder builder) throws Exception {

    final ConnectionFactory factory = new ConnectionFactory();

    factory.setHost(builder.host());

    if (builder.port() != -1) {
      factory.setPort(builder.port());
    }

    factory.setConnectionTimeout(builder.timeout());

    factory.setAutomaticRecoveryEnabled(builder.autoRecovery());
    factory.setNetworkRecoveryInterval(builder.networkRecoveryInterval());

    if (builder.credentials() != null && builder.credentials() instanceof BasicCredentials) {
      BasicCredentials basic = (BasicCredentials) builder.credentials();
      factory.setUsername(basic.username());
      factory.setPassword(basic.password());
    }


    this.connection = factory.newConnection();
    this.channel = connection.createChannel();

  }


  @Override
  public void close() throws Exception {
    if (this.channel != null && this.channel.isOpen()) {
      this.channel.close();
    }
    if (this.connection != null && this.connection.isOpen()) {
      this.connection.close();
    }
  }

}
