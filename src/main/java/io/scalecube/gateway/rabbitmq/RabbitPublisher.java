package io.scalecube.gateway.rabbitmq;

import com.rabbitmq.client.Channel;

/**
 * Publisher to rabbit mq messages.
 */
public class RabbitPublisher extends RmqChannel {

  /**
   * initialize rabbit mq publisher
   * 
   * @param builder of rabbit broker.
   * @throws exception if failed.
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
