package io.scalecube.gateway.rabbitmq;

import static org.junit.Assert.assertTrue;

import io.scalecube.examples.services.GreetingRequest;
import io.scalecube.examples.services.GreetingResponse;
import io.scalecube.examples.services.GreetingService;
import io.scalecube.examples.services.GreetingServiceImpl;
import io.scalecube.services.Microservices;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RabbitGatewayIT {

  private static final String TOPIC_GREETING_SERVICE_REQUESTS = "hello_world_requests";

  private static final String TOPIC_GREETING_SERVICE_RESPONSES = "hello_world_responses";

  private static final Topic REQUEST_TOPIC =
      Topic.builder().name(TOPIC_GREETING_SERVICE_REQUESTS).build();

  private static final Topic RESPONSE_TOPIC =
      Topic.builder().name(TOPIC_GREETING_SERVICE_RESPONSES).build();

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
        .listen(String.class).subscribe(onNext -> {
          service.greeting(onNext.toString()).whenComplete((response, ex) -> {
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
        .plain().listen(String.class).subscribe(onNext -> {
          timeLatch.countDown();
          assertTrue(onNext.equals("Hello joe"));
        });

    publisher.plain().publish(REQUEST_TOPIC, "joe");
    System.out.println(System.currentTimeMillis() - start);
    timeLatch.await(100, TimeUnit.SECONDS);
    System.out.println(System.currentTimeMillis() - start);
  }


  @Test
  public void test_rabbit_mq_greeting_proto_request_reply() throws Exception {
    // Create Micro-cluster for the api gateway cluster Member
    Microservices gateway = Microservices.builder().build();

    // Create Micro-cluster for the service provider
    Microservices serviceProvider = Microservices.builder()
        .port(3000)
        .portAutoIncrement(false)
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
    RMQ rmq = RMQ.builder().host("localhost").build();


    rmq.topic(REQUEST_TOPIC).proto()
        .listen(GreetingRequest.class).subscribe(onNext -> {
          service.greetingRequest(onNext).whenComplete((response, ex) -> {
            try {
              rmq.publish(RESPONSE_TOPIC, response);
            } catch (Exception e) {
            }
          });
        });

    rmq.proto().publish(REQUEST_TOPIC, new GreetingRequest("joe"));
    System.out.println("done1");
  }


  @Test
  public void test_rabbit_mq_greeting_byte_request_reply() throws Exception {
    // Create Micro-cluster for the api gateway cluster Member
    Microservices gateway = Microservices.builder()
        .port(9000)
        .portAutoIncrement(false)
        .build();

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
    
    serviceQueue.topic(REQUEST_TOPIC).listen().subscribe(onNext -> {
      
      service.greeting(new String(onNext)).whenComplete((response, ex) -> {
        try {
          System.out.println("service response " + response);
          serviceQueue.publish(RESPONSE_TOPIC, response.getBytes());
        } catch (Exception e) {
        }
      });
    });

    CountDownLatch timeLatch = new CountDownLatch(1);
    long start = System.currentTimeMillis();
    // RabbitMQ service client.
    RMQ publisher = RMQ.builder().host("localhost").build();
    publisher.topic(RESPONSE_TOPIC).listen().subscribe(onNext -> {
      timeLatch.countDown();
      System.out.println("RMQ response " + onNext);
      assertTrue(onNext.equals("Hello joe"));
    });

    publisher.plain().publish(REQUEST_TOPIC, "joe");
    System.out.println(System.currentTimeMillis() - start);
    timeLatch.await(100, TimeUnit.SECONDS);
    System.out.println(System.currentTimeMillis() - start);
  }
}
