package io.scalecube.gateway.rabbitmq;

import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Publisher to rabbit mq messages.
 */
public class RabbitPublisher extends RmqChannel {

  /**
   * initialize rabbit mq publisher
   * 
   * @param host of rabbit broker.
   * @param port of rabbit mq broker.
   * @param timeout connection timeout to rabbit mq broker.
   * @param credentials to rabbit mq broker.
   * @throws IOException if failed.
   * @throws TimeoutException if failed.
   */

  public RabbitPublisher(Rmq.Builder builder) throws Exception {
    super(builder);
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
}
