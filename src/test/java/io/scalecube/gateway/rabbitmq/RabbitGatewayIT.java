package io.scalecube.gateway.rabbitmq;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import io.scalecube.examples.services.GreetingRequest;
import io.scalecube.examples.services.GreetingResponse;
import io.scalecube.examples.services.GreetingService;
import io.scalecube.examples.services.GreetingServiceImpl;
import io.scalecube.services.Microservices;

import org.junit.Test;

import rx.Subscription;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RabbitGatewayIT {

  private static final String TOPIC_GREETING_SERVICE_REQUESTS_BYTES = "hello_world_requests_bytes";
  private static final String TOPIC_GREETING_SERVICE_RESPONSES_BYTES = "hello_world_responses_bytes";

  private static final Topic REQUEST_TOPIC_BYTES = Topic.builder().name(TOPIC_GREETING_SERVICE_REQUESTS_BYTES).build();
  private static final Topic RESPONSE_TOPIC_BYTES = Topic.builder().name(TOPIC_GREETING_SERVICE_RESPONSES_BYTES).build();

  
  
  private static final String TOPIC_GREETING_SERVICE_REQUESTS_PLAIN = "hello_world_requests_plain";
  private static final String TOPIC_GREETING_SERVICE_RESPONSES_PLAIN = "hello_world_responses_plain";

  private static final Topic REQUEST_TOPIC_PLAIN = Topic.builder().name(TOPIC_GREETING_SERVICE_REQUESTS_PLAIN).build();
  private static final Topic RESPONSE_TOPIC_PLAIN = Topic.builder().name(TOPIC_GREETING_SERVICE_RESPONSES_PLAIN).build();

  
  private static final String TOPIC_GREETING_SERVICE_REQUESTS_JSON = "hello_world_requests_json";
  private static final String TOPIC_GREETING_SERVICE_RESPONSES_JSON = "hello_world_responses_json";

  private static final Topic REQUEST_TOPIC_JSON = Topic.builder().name(TOPIC_GREETING_SERVICE_REQUESTS_JSON).build();
  private static final Topic RESPONSE_TOPIC_JSON = Topic.builder().name(TOPIC_GREETING_SERVICE_RESPONSES_JSON).build();


  
  
  private static final String TOPIC_GREETING_SERVICE_REQUESTS_PROTO = "hello_world_requests_proto";
  private static final String TOPIC_GREETING_SERVICE_RESPONSES_PROTO = "hello_world_responses_proto";

  private static final Topic REQUEST_TOPIC_PROTO = Topic.builder().name(TOPIC_GREETING_SERVICE_REQUESTS_PROTO).build();
  private static final Topic RESPONSE_TOPIC_PROTO = Topic.builder().name(TOPIC_GREETING_SERVICE_RESPONSES_PROTO).build();
  
  


  
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

    serviceQueue.topic(REQUEST_TOPIC_PLAIN).plain()
        .listen(String.class).subscribe(onNext -> {
          service.greeting(onNext.toString()).whenComplete((response, ex) -> {
            try {
              serviceQueue.publish(REQUEST_TOPIC_PLAIN, response);
            } catch (Exception e) {
            }
          });
        });

    CountDownLatch timeLatch = new CountDownLatch(1);

    // RabbitMQ service client.
    RMQ publisher = RMQ.builder().host("localhost").build();

    publisher.topic(RESPONSE_TOPIC_PLAIN).plain()
        .listen(String.class).subscribe(onNext -> {

          assertTrue(onNext.equals("Hello joe"));
          timeLatch.countDown();
        });

    long start = System.currentTimeMillis();
    publisher.plain().publish(REQUEST_TOPIC_PLAIN, "joe");
    System.out.println(System.currentTimeMillis() - start);
    timeLatch.await(1, TimeUnit.SECONDS);
    System.out.println(System.currentTimeMillis() - start);
  }


  @Test
  public void test_rabbit_mq_greeting_proto_request_reply() throws Exception {
    // Create Micro-cluster for the api gateway cluster Member
    Microservices gateway = Microservices.builder().build();

    // Create Micro-cluster for the service provider
    Microservices serviceProvider = Microservices.builder()
        .port(3000)
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
    RMQ publisher = RMQ.builder().host("localhost").build();


    publisher.topic(REQUEST_TOPIC_PROTO).proto()
        .listen(GreetingRequest.class).subscribe(onNext -> {
          service.greetingRequest(onNext).whenComplete((response, ex) -> {
            try {
              publisher.proto().publish(REQUEST_TOPIC_PROTO, response);
            } catch (Exception e) {
            }
          });
        });


    CountDownLatch timeLatch = new CountDownLatch(1);
    RMQ responseSubscriber = RMQ.builder().host("localhost").build();
    responseSubscriber.proto().topic(RESPONSE_TOPIC_PROTO).listen(GreetingResponse.class)
        .subscribe(onNext -> {
          System.out.println(onNext.result());
          assertTrue(onNext.result().equals("Hello joe"));
          timeLatch.countDown();
        });

    long start = System.currentTimeMillis();
    responseSubscriber.proto().publish(REQUEST_TOPIC_PROTO, new GreetingRequest("joe"));
    System.out.println("test_rabbit_mq_greeting_proto_request_reply sent ms:" + (System.currentTimeMillis() - start));
    timeLatch.await(2, TimeUnit.SECONDS);
    System.out
        .println("test_rabbit_mq_greeting_proto_request_reply recived ms:" + (System.currentTimeMillis() - start));
  }


  @Test
  public void test_rabbit_mq_greeting_json_request_reply() throws Exception {
    // Create Micro-cluster for the api gateway cluster Member
    Microservices gateway = Microservices.builder().build();

    // Create Micro-cluster for the service provider
    Microservices serviceProvider = Microservices.builder()
        .port(3000)
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
    RMQ publisher = RMQ.builder().host("localhost").build();


    Subscription reqSubscription = publisher.topic(REQUEST_TOPIC_JSON).json()
        .listen(GreetingRequest.class).subscribe(onNext -> {
          service.greetingRequest(onNext).whenComplete((response, ex) -> {
            try {
              publisher.json().publish(RESPONSE_TOPIC_JSON, response);
            } catch (Exception e) {
            }
          });
        });


    CountDownLatch timeLatch = new CountDownLatch(1);
    RMQ responseSubscriber = RMQ.builder().host("localhost").build();
    Subscription subscription = responseSubscriber.json().topic(RESPONSE_TOPIC_JSON).listen(GreetingResponse.class)
        .subscribe(onNext -> {
          System.out.println(onNext.result());
          assertTrue(onNext.result().equals("Hello joe"));
          
          timeLatch.countDown();
        });

    long start = System.currentTimeMillis();
    responseSubscriber.json().publish(REQUEST_TOPIC_JSON, new GreetingRequest("joe"));
    System.out.println("test_rabbit_mq_greeting_json_request_reply sent ms:" + (System.currentTimeMillis() - start));
    timeLatch.await(2, TimeUnit.SECONDS);
    System.out
        .println("test_rabbit_mq_greeting_json_request_reply recived ms:" + (System.currentTimeMillis() - start));

    subscription.unsubscribe();
    reqSubscription.unsubscribe();
  }
  

  @Test
  public void test_rabbit_mq_greeting_byte_request_reply() throws Exception {
    // Create Micro-cluster for the api gateway cluster Member
    Microservices gateway = Microservices.builder()
        .port(9000)
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
    RMQ consumer = RMQ.builder().host("localhost").build();

    consumer.topic(REQUEST_TOPIC_BYTES).listen().subscribe(onNext -> {

      service.greeting(new String(onNext)).whenComplete((response, ex) -> {
        try {
          System.out.println("service response " + response);
          consumer.publish(RESPONSE_TOPIC_BYTES, response.getBytes());
        } catch (Exception e) {
        }
      });
    });

    CountDownLatch timeLatch = new CountDownLatch(1);
    long start = System.currentTimeMillis();
    // RabbitMQ service client.
    RMQ publisher = RMQ.builder().host("localhost").build();
    publisher.topic(RESPONSE_TOPIC_BYTES).listen().subscribe(onNext -> {
      System.out.println("RMQ response " + new String(onNext));
      assertTrue(new String(onNext).equals("Hello joe"));
      timeLatch.countDown();
    });

    publisher.publish(REQUEST_TOPIC_BYTES, "joe".getBytes());
    System.out.println("test_rabbit_mq_greeting_byte_request_reply sent ms:" + (System.currentTimeMillis() - start));
    timeLatch.await(10, TimeUnit.SECONDS);
    System.out.println("test_rabbit_mq_greeting_byte_request_reply recived ms:" + (System.currentTimeMillis() - start));
  }

  @Test
  public void test_rabbit_mq_exchange_greeting_byte_request_reply() throws Exception {
    // Create Micro-cluster for the api gateway cluster Member
    Microservices gateway = Microservices.builder()
        .port(9000)
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

    Exchange exchange = Exchange.builder().name("in").build();

    serviceQueue.exchange(exchange, REQUEST_TOPIC_PLAIN, "");

    serviceQueue.topic(REQUEST_TOPIC_PLAIN).plain().listen(String.class).subscribe(onNext -> {
      service.greeting(new String(onNext)).whenComplete((response, ex) -> {
        try {
          System.out.println("service response " + response);
          serviceQueue.plain().publish(RESPONSE_TOPIC_PLAIN, response);
        } catch (Exception e) {
        }
      });
    });

    CountDownLatch timeLatch = new CountDownLatch(1);
    long start = System.currentTimeMillis();
    // RabbitMQ service client.
    RMQ publisher = RMQ.builder().host("localhost").build();

    publisher.topic(RESPONSE_TOPIC_PLAIN).plain().listen(String.class)
        .subscribe(onNext -> {
          System.out.println("RMQ response " + onNext);
          assertTrue(onNext.equals("Hello joe"));
          timeLatch.countDown();
        });

    publisher.plain().publish(REQUEST_TOPIC_PLAIN, "joe");
    System.out
        .println("test_rabbit_mq_exchange_greeting_byte_request_reply sent ms:" + (System.currentTimeMillis() - start));
    timeLatch.await(1, TimeUnit.SECONDS);
    System.out
        .println("test_rabbit_mq_exchange_greeting_byte_request_reply recived" + (System.currentTimeMillis() - start));
  }
}
