package com.health.mental.service;

import com.health.mental.config.MentalHealthConfig;
import com.health.mental.domain.dto.IpAddressDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class IpAddressService {
  private static final Logger logger = LoggerFactory.getLogger(IpAddressService.class);

  private static final String DEFAULT_IP_ADDRESS_HEADER = "188.27.129.229";

  private final WebClient webClient;

  public IpAddressService(
      final WebClient.Builder webClientBuilder, final MentalHealthConfig mentalHealthConfig) {
    final var ipAddressBaseUrl = mentalHealthConfig.getIpAddressBaseUrl();
    this.webClient = webClientBuilder.baseUrl(ipAddressBaseUrl).build();
  }

  public String fetchIpAddress() {
    try {
      final var response = webClient.get().retrieve().bodyToMono(IpAddressDTO.class).block();
      return response == null || response.ip() == null ? DEFAULT_IP_ADDRESS_HEADER : response.ip();
    } catch (final Exception e) {
      logger.error("Error fetching IP address: {}", e.getMessage());
      return DEFAULT_IP_ADDRESS_HEADER;
    }
  }
}
