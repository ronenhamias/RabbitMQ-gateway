package io.scalecube.gateway.rabbitmq;

import static org.junit.Assert.assertTrue;

import io.scalecube.examples.services.GreetingService;
import io.scalecube.examples.services.GreetingServiceImpl;
import io.scalecube.services.Microservices;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RabbitGatewayIT {

  private static final String TOPIC_GREETING_SERVICE_REQUESTS = "hello_world_requests";

  private static final String TOPIC_GREETING_SERVICE_RESPONSES = "hello_world_responses";
  
  private static final Topic REQUEST_TOPIC = Topic.builder().name(TOPIC_GREETING_SERVICE_REQUESTS).build();
  
  private static final Topic RESPONSE_TOPIC = Topic.builder().name(TOPIC_GREETING_SERVICE_RESPONSES).build();
  
  @Test
  public void test_rabbit_mq_greeting_plain_request_reply() throws Exception {
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
    
    serviceQueue.topic(REQUEST_TOPIC).plain()
        .listen().subscribe(onNext -> {
          service.greeting(onNext.toString()).whenComplete((response,ex)->{
            try {
              serviceQueue.publish(RESPONSE_TOPIC, response);
            } catch (Exception e) {
            }
          });
        });

    CountDownLatch timeLatch = new CountDownLatch(1);
    long start = System.currentTimeMillis();
    // RabbitMQ service client.
    RMQ publisher = RMQ.builder().host("localhost").build();
    
    publisher.topic(RESPONSE_TOPIC)
        .plain().listen().subscribe(onNext -> {
          timeLatch.countDown();
          assertTrue(onNext.equals("Hello joe"));
        });

    publisher.plain().publish(REQUEST_TOPIC, "joe");
    System.out.println( System.currentTimeMillis() - start);
    timeLatch.await(100, TimeUnit.SECONDS);
    System.out.println( System.currentTimeMillis() - start);
  }
  
  @Test
  public void test_rabbit_mq_greeting_byte_request_reply() throws Exception {
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
   
    serviceQueue.topic(REQUEST_TOPIC).plain().listen().subscribe(onNext -> {
          service.greeting(onNext.toString()).whenComplete((response,ex)->{
            try {
              serviceQueue.publish(RESPONSE_TOPIC, response);
            } catch (Exception e) {
            }
          });
        });

    CountDownLatch timeLatch = new CountDownLatch(1);
    long start = System.currentTimeMillis();
    // RabbitMQ service client.
    RMQ publisher = RMQ.builder().host("localhost").build();
    publisher.topic(RESPONSE_TOPIC).listen().subscribe(onNext->{
      timeLatch.countDown();
      assertTrue(onNext.equals("Hello joe"));
    });
    
    publisher.plain().publish(REQUEST_TOPIC, "joe");
    System.out.println( System.currentTimeMillis() - start);
    timeLatch.await(100, TimeUnit.SECONDS);
    System.out.println( System.currentTimeMillis() - start);
  }
}