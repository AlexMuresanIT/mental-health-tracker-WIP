package com.health.mental.service;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.wiremock.spring.EnableWireMock;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableWireMock
public class LocationServiceTest {
  private static final String TEST_IP_ADDRESS = "188.27.129.229";

  private static final String LOCATION_URL = "/json/" + TEST_IP_ADDRESS;

  private static final String TEST_DATA_PATH = "test-data/demo-location-response.json";
  private static final String CONTENT_TYPE = "Content-Type";

  @Autowired private LocationService locationService;

  @Test
  void shouldGetLocationForIpAddress() throws IOException {
    initializeURL(LOCATION_URL, TEST_DATA_PATH);
    final var location = locationService.getLocationForIpAddress(TEST_IP_ADDRESS);

    assertThat(location).isNotNull();
    assertThat(location.city()).isEqualTo("Cluj-Napoca");
    assertThat(location.country()).isEqualTo("Romania");
    assertThat(location.latitude()).isEqualTo(46.7656);
    assertThat(location.longitude()).isEqualTo(23.5945);
  }

  @Test
  void shouldGetDefaultLocation() {
    final var location = locationService.getLocationForIpAddress("invalid-ip");

    assertThat(location).isNotNull();
    assertThat(location.city()).isEqualTo("Unknown");
    assertThat(location.country()).isEqualTo("Unknown");
    assertThat(location.latitude()).isEqualTo(0.0);
    assertThat(location.longitude()).isEqualTo(0.0);
  }

  private static void initializeURL(final String url, final String testDataPath)
      throws IOException {
    final var stream =
        Objects.requireNonNull(
            LocationServiceTest.class.getClassLoader().getResourceAsStream(testDataPath));
    stubFor(
        get(urlMatching(url))
            .willReturn(
                aResponse()
                    .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(stream.readAllBytes())));
  }
}
