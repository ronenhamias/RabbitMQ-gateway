package io.scalecube.examples.services;

public class GreetingResponse {

  private String result;

  public GreetingResponse(String result) {
    this.result = result;
  }

  public String result(){
    return this.result;
  }
}
