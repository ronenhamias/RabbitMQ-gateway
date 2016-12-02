package io.scalecube.examples.services;

import io.protostuff.Tag;

public class GreetingResponse {

  @Tag(value=1)
  private String result;

  public GreetingResponse(String result) {
    this.result = result;
  }

  public String result(){
    return this.result;
  }
}
