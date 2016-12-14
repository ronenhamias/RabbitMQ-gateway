package io.scalecube.gateway.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by noam on 11/12/16.
 */
public class RabbitPublisher {

  private final ConnectionFactory factory;

  final Connection connection;

  final Channel channel;

  private final Subject<byte[], byte[]> outboundMessagesSubject;

  private MessageSerialization serialization;

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

  public void subscribe(Exchange exchange) throws Exception {

    channel.exchangeDeclare(exchange.name(),
        exchange.type(),
        exchange.durable(),
        exchange.autoDelete(),
        exchange.autoDelete(),
        exchange.properties());
  }

  public Channel channel() {
    return this.channel;
  }
}
