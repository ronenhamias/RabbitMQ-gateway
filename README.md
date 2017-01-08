[![Build Status](https://travis-ci.org/scalecube/RabbitMQ.svg?branch=master)](https://travis-ci.org/scalecube/RabbitMQ)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.scalecube/scalecube-rabbitmq/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.scalecube/scalecube-rabbitmq)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/48e0f5d4f9b64924bccafadf562697f3)](https://www.codacy.com/app/ronenn/RabbitMQ?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=scalecube/RabbitMQ&amp;utm_campaign=Badge_Grade)
[![codecov](https://codecov.io/gh/scalecube/RabbitMQ/branch/master/graph/badge.svg)](https://codecov.io/gh/scalecube/RabbitMQ)

# RabbitMQ
RabbitMQ integration with scalecube services

Features:
- Java 8, RxJava Observers
- serialization , byte[], string , json, google protocol buffers 
- Exchange , Queue

# Basic Usage


Given a micro-service located somewhere in the cluster.
we wish to communicated with it via rabbit mq api gateway and json format:

``` java

 // Provision a Micro-cluster for the service provider
Microservices serviceProvider = Microservices.builder()
    // serviceProvider will join the gateway micro-cluster
    // this Micro-cluster provision GreetingService microservice instance
    .services(new GreetingServiceImpl())
    .build();



// Acquire proxy to the microservice from any member (see scalecube microservices for more info).
GreetingService service = gateway.proxy()
    .api(GreetingService.class)
    .create();  

```


``` java

// declare topics:
private static final Topic REQUEST_TOPIC = Topic.builder().name("TOPIC_GREETING_SERVICE_REQUESTS").build();

private static final Topic RESPONSE_TOPIC = Topic.builder().name("TOPIC_GREETING_SERVICE_RESPONSES").build();


// RabbitMQ API Gateway on rabbit localhost.
RMQ serviceQueue = RMQ.builder().json().host("localhost").build();

// listen on GreetingRequest from request topic.
serviceQueue.topic(REQUEST_TOPIC)
	.listen(GreetingRequest.class).subscribe(onNext -> {

// call the microservice onNext MQ request.	
service.greeting(onNext).whenComplete((response, ex) -> {
    try {
      serviceQueue.publish(RESPONSE_TOPIC, response);
    } catch (Exception e) {
    	//...
    }
  });
});


RMQ publisher = RMQ.builder().json().host("localhost").build();

// listen on GreetingResponse from response topic.
publisher.topic(RESPONSE_TOPIC).listen(GreetingResponse.class).subscribe(onNext -> {
  // do stuff with response from service.
});


publisher.publish(REQUEST_TOPIC, new GreetingRequest("joe"));


```

    
