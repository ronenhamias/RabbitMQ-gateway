package io.scalecube.gateway.rabbitmq;

import io.scalecube.examples.services.GreetingService;
import io.scalecube.examples.services.GreetingServiceImpl;
import io.scalecube.services.Microservices;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * Example of RabbitMQ API Gateway pattern.
 */
public class RabbitMQApiGatewayExample {

  private static final String TOPIC_GREETING_SERVICE_REQUESTS = "hello_world_requests";

  private static final String TOPIC_GREETING_SERVICE_RESPONSES = "hello_world_responses";

  private static final Topic REQUEST_TOPIC = Topic.builder().name(TOPIC_GREETING_SERVICE_REQUESTS).build();

  private static final Topic RESPONSE_TOPIC = Topic.builder().name(TOPIC_GREETING_SERVICE_RESPONSES).build();

  /**
   * API Gateway example demonstrate the basic concept of an API Gateway "adapter" calling a microservice. the example
   * creates a micro-cluster consists of:
   * <li>gateway node with API Gateway.
   * <li>GreetingService node.
   * 
   * <p>
   * RabbitMQ listener receives message on queue hello_world_topic.
   * 
   * <p>
   * Using a service proxy it calls provider node of GreetingService.
   * 
   */
  public static void main(String[] args) throws Exception {

    // Create Micro-cluster for the api gateway cluster Member
    Microservices gateway = Microservices.builder().build();

    // Create Micro-cluster for the service provider
    Microservices serviceProvider = Microservices.builder()
        // serviceProvider will join the gateway micro-cluster
        .seeds(gateway.cluster().address())
        .port(7000)
        // this Micro-cluster provision GreetingService microservice instance
        .services(new GreetingServiceImpl())
        .build();


    // Create service proxy from gateway micro-cluster.
    GreetingService service = gateway.proxy()
        .api(GreetingService.class)
        .create();

    // RabbitMQ API Gateway.
    Rmq serviceQueue = Rmq.builder().plain().host("localhost").build();

    serviceQueue.topic(REQUEST_TOPIC)
        .listen(String.class).subscribe(onNext -> {
          service.greeting(onNext.toString()).whenComplete((response, ex) -> {
            try {
              serviceQueue.publish(RESPONSE_TOPIC, response);
            } catch (Exception e) {
            }
          });
        });



    CountDownLatch timeLatch = new CountDownLatch(10000);
    long start = System.currentTimeMillis();
    // RabbitMQ service client.
    Rmq publisher = Rmq.builder().plain().host("localhost").build();

    publisher.topic(RESPONSE_TOPIC).listen(String.class).subscribe(onNext -> {
      System.out.println(onNext + timeLatch.getCount());
      timeLatch.countDown();
    });

    CompletableFuture.supplyAsync(()->{
      for (int i = 0; i < 100000; i++) {
        try {
          publisher.publish(REQUEST_TOPIC, "joe");
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      return null;
    });
    System.out.println(System.currentTimeMillis() - start);
    timeLatch.await(100, TimeUnit.SECONDS);
    System.out.println(System.currentTimeMillis() - start);
    System.exit(0);
  }
}
