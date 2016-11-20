package io.scalecube.gateway.rabbitmq;

import io.scalecube.transport.Message;

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

public class RabbitListener {
  private final ConnectionFactory factory;
  final Connection connection;
  final Channel channel;
  private final Subject<Object, Object> incomingMessagesSubject;
  private MessageSerialization serialization;
  
  public RabbitListener(String host) throws Exception {
    this.factory = new ConnectionFactory();
    this.factory.setHost(host);
    this.connection = factory.newConnection();
    this.channel= connection.createChannel();
    this.incomingMessagesSubject = PublishSubject.<Object>create().toSerialized();  
  }
  
  public void subscribe(String name) throws Exception{
    channel.queueDeclare(name, true, false, false, null);
    final Consumer consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        try {
          incomingMessagesSubject.onNext(serialization.deserialize(body));
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        try {
        } finally {
          channel.basicAck(envelope.getDeliveryTag(), false);
        }
      }
    };
    boolean autoAck = false;
    channel.basicConsume(name, autoAck, consumer);
  }
  
  public Observable<Object> listen(MessageSerialization serialization){
    this.serialization = serialization;
    return incomingMessagesSubject.onBackpressureBuffer();
  }

  public Channel channel() {
    return this.channel;
  }
}
