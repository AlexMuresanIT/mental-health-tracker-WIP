package com.health.mental.service;

import static com.health.mental.TestUtil.initializeURL;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.wiremock.spring.EnableWireMock;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableWireMock
public class IpAddressServiceTest {
  private static final String DEFAULT_IP_ADDRESS_HEADER = "188.27.129.228";

  private static final String IP_ADDRESS_URL = "https://api.ipify.org?format=json";

  private static final String TEST_IP_ADDRESS = "test-data/ip-address-test.json";

  @Autowired private IpAddressService ipAddressService;

  @Test
  void shouldReturnIpAddress() throws IOException {
    initializeURL(IP_ADDRESS_URL, TEST_IP_ADDRESS);

    final var ipAddress = ipAddressService.fetchIpAddress();

    assertThat(ipAddress).isNotNull();
    assertThat(ipAddress).isNotEqualTo(DEFAULT_IP_ADDRESS_HEADER);
  }
}
