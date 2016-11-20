package io.scalecube.gateway.rabbitmq;

import io.scalecube.examples.services.GreetingService;
import io.scalecube.examples.services.GreetingServiceImpl;
import io.scalecube.gateway.rabbitmq.RMQ;
import io.scalecube.services.Microservices;


/**
 * Example of RabbitMQ API Gateway pattern.
 */
public class RabbitMQApiGatewayExample {

  private static final String TOPIC_GREETING_SERVICE = "hello_world_topic";

  /**
   * API Gateway example demonstrate the basic concept of an API Gateway "adapter" calling a microservice. the example
   * reates a micro-cluster consists of:
   * <li>gateway node with API Gateway.
   * <li>GreetingService node.
   * 
   * <p>RabbitMQ listener receives message on queue hello_world_topic.
   * 
   * <p>Using a service proxy it calls provider node of GreetingService.
   * 
   */
  public static void main(String[] args) throws Exception {

    // Create Micro-cluster for the api gateway cluster Member
    Microservices gateway = Microservices.builder().build();

    // Create Micro-cluster for the service provider
    Microservices serviceProvider = Microservices.builder()
        // serviceProvider will join the gateway micro-cluster
        .seeds(gateway.cluster().address())
        // this Micro-cluster provision GreetingService microservice instance
        .services(new GreetingServiceImpl())
        .build();


    // Create service proxy from gateway micro-cluster.
    GreetingService service = gateway.proxy()
        .api(GreetingService.class)
        .create();

    // RabbitMQ API Gateway.
    RMQ serviceQueue = RMQ.builder().host("localhost").build();
    serviceQueue.queue(TOPIC_GREETING_SERVICE).plain()
        .listen().subscribe(onNext -> {
          service.greeting(onNext.toString()).whenComplete((response,ex)->{
            try {
              serviceQueue.publish("response_queue", response);
            } catch (Exception e) {
            }
          });
        });

    
    // RabbitMQ service client.
    RMQ publisher = RMQ.builder().host("localhost").build();
    publisher.queue("response_queue").plain().listen().subscribe(onNext->{
          System.out.println(onNext);
        });
    
    publisher.plain().publish(TOPIC_GREETING_SERVICE, "joe");

  }
}
