package io.scalecube.gateway.rabbitmq;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BasicCredentialsTest {

  @Test
  public void test_basic_cred() {
    
    BasicCredentials cred = new BasicCredentials("username", "password");
    assertEquals( cred.password(), "password");
    assertEquals( cred.username(), "username");
    
  }
}
