package io.scalecube.gateway.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitListener implements AutoCloseable {

  private final ConnectionFactory factory;

  final Connection connection;

  final Channel channel;

  private final Subject<byte[], byte[]> incomingMessagesSubject;

  private MessageSerialization serialization;

  /**
   * initialize rabbit mq listener
   * 
   * @param host of rabbit broker.
   * @param port of rabbit mq broker.
   * @param timeout connection timeout to rabbit mq broker.
   * @param credentials to rabbit mq broker.
   * @param serialization to be used when sending messages.
   * @throws IOException if failed.
   * @throws TimeoutException if failed.
   */
  public RabbitListener(String host, int port, int timeout, Credentials credentials, MessageSerialization serialization)
      throws Exception {
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
    this.incomingMessagesSubject = PublishSubject.<byte[]>create().toSerialized();
  }

  /**
   * Declare an exchange, via an interface that allows the complete set of arguments.
   * 
   * @see com.rabbitmq.client.AMQP.Exchange.Declare
   * @see com.rabbitmq.client.AMQP.Exchange.DeclareOk <br>
   *      exchange the name of the exchange <br>
   *      type the exchange type <br>
   *      durable true if we are declaring a durable exchange (the exchange will survive a server restart) <br>
   *      autoDelete true if the server should delete the exchange when it is no longer in use <br>
   *      internal true if the exchange is internal, i.e. can't be directly published to by a client. <br>
   *      arguments other properties (construction arguments) for the exchange
   * @throws java.io.IOException if an error is encountered
   */
  public void subscribe(Exchange exchange, Topic topic, String routingKey) throws Exception {

    channel.exchangeDeclare(exchange.exchange(),
        exchange.type(),
        exchange.durable(),
        exchange.autoDelete(),
        exchange.autoDelete(),
        exchange.properties());

    channel.queueDeclare(topic.name(),
        topic.durable(),
        topic.exclusive(),
        topic.autoDelete(), null);

    channel.queueBind(topic.name(), exchange.exchange(), routingKey);

    final Consumer consumer = createConsumer(channel);
    boolean autoAck = false;
    channel.basicConsume(topic.name(), autoAck, consumer);
  }

  /**
   * listen on rabbit mq topic.
   * 
   * @param topic to subscribe on.
   * @throws Exception if failed to subscribe.
   */
  public void subscribe(Topic topic) throws Exception {
    channel.queueDeclare(topic.name(),
        topic.durable(),
        topic.exclusive(),
        topic.autoDelete(), null);

    final Consumer consumer = createConsumer(channel);
    boolean autoAck = false;
    channel.basicConsume(topic.name(), autoAck, consumer);
  }

  private Consumer createConsumer(Channel channel) {
    final Consumer consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
          throws IOException {
        try {
          incomingMessagesSubject.onNext(body);
        } catch (Exception e) {
          // TODO: log exception.
          e.printStackTrace();
        }
        try {
          // do nothing.
        } finally {
          channel.basicAck(envelope.getDeliveryTag(), false);
        }
      }
    };
    return consumer;
  }

  @SuppressWarnings("unchecked")
  public <T> Observable<T> listen(Class<T> class1) {
    return (Observable<T>) incomingMessagesSubject
        .onBackpressureBuffer().map(onNext -> deserialize((byte[]) onNext, class1));
  }

  public <T> Observable<byte[]> listen() {
    return incomingMessagesSubject.onBackpressureBuffer();
  }

  private <T> T deserialize(byte[] onNext, Class<T> class1) {
    try {
      return this.serialization.deserialize(onNext, class1);
    } catch (Exception e) {
      return null;
    }
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
