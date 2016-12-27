package io.scalecube.gateway.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Publisher to rabbit mq messages.
 */
public class RabbitPublisher implements AutoCloseable{

  private final ConnectionFactory factory;

  final Connection connection;

  final Channel channel;

  private final Subject<byte[], byte[]> outboundMessagesSubject;

  private MessageSerialization serialization;

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
  public RabbitPublisher(String host, int port, int timeout, Credentials credentials,
      MessageSerialization serialization) throws IOException, TimeoutException {
    this.factory = new ConnectionFactory();
    this.serialization = serialization;

    this.factory.setHost(host);

    if (port != -1) {
      this.factory.setPort(port);
    }

    this.factory.setConnectionTimeout(timeout);

    if (credentials != null) {
      if (credentials instanceof BasicCredentials) {
        BasicCredentials basic = (BasicCredentials) credentials;
        this.factory.setUsername(basic.username());
        this.factory.setPassword(basic.password());
      }
    }

    this.connection = factory.newConnection();
    this.channel = connection.createChannel();
    this.outboundMessagesSubject = PublishSubject.<byte[]>create().toSerialized();
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
