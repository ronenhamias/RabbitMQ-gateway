package io.scalecube.examples.services;

public class GreetingRequest {

  private String name;

  private GreetingRequest(){};
  
  public String getName() {
    return name;
  }

  public GreetingRequest(String name) {
    this.name =  name;
  }

  public String name(){
    return name;
  }
}
