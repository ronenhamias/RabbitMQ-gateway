package io.scalecube.examples.services;

import io.protostuff.Tag;

public class GreetingRequest {

  @Tag(value = 1)
  private String name;

  private GreetingRequest() {};

  public String getName() {
    return name;
  }

  public GreetingRequest(String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }

}
