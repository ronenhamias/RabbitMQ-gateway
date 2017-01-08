package io.scalecube.gateway.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import rx.subjects.PublishSubject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Publisher to rabbit mq messages.
 */
public class RabbitPublisher implements AutoCloseable{

  private final Connection connection;

  private final Channel channel;

  /**
   * initialize rabbit mq publisher
   * 
   * @param host of rabbit broker.
   * @param port of rabbit mq broker.
   * @param timeout connection timeout to rabbit mq broker.
   * @param credentials to rabbit mq broker.
   * @param serialization to be used when sending messages.
   * @throws IOException if failed.
   * @throws TimeoutException if failed.
   */
  public RabbitPublisher(String host, int port, int timeout, Credentials credentials) throws IOException, TimeoutException {
    final ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(host);

    if (port != -1) {
      factory.setPort(port);
    }

    factory.setConnectionTimeout(timeout);

    if (credentials != null) {
      if (credentials instanceof BasicCredentials) {
        BasicCredentials basic = (BasicCredentials) credentials;
        factory.setUsername(basic.username());
        factory.setPassword(basic.password());
      }
    }

    this.connection = factory.newConnection();
    this.channel = connection.createChannel();
  }

  /**
   * subscribe to rabbit mq exchange.
   * 
   * @param exchange to subscribe.
   * @throws Exception if failed.
   */
  public void subscribe(Exchange exchange) throws Exception {

    channel.exchangeDeclare(exchange.exchange(),
        exchange.type(),
        exchange.durable(),
        exchange.autoDelete(),
        exchange.autoDelete(),
        exchange.properties());
  }

  public Channel channel() {
    return this.channel;
  }

  @Override
  public void close() throws Exception {
    if (this.channel != null) {
      this.channel.close();
    }
    if (this.connection != null) {
      this.connection.close();
    }
  }
}
