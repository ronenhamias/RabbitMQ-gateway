package io.scalecube.gateway.rabbitmq;

public class BasicCredentials implements Credentials {

  private final String username;

  private final String password;

  public BasicCredentials(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String username() {
    return username;
  }

  public String password() {
    return password;
  }

}
