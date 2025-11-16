package com.health.mental.service;

import com.health.mental.config.MentalHealthConfig;
import com.health.mental.domain.Location;
import com.health.mental.domain.dto.LocationDTO;
import com.health.mental.mapper.LocationMapper;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

@Service
public class LocationService {
  private static final Logger log = LoggerFactory.getLogger(LocationService.class);

  private final WebClient client;
  private final LocationMapper locationMapper;

  public LocationService(
      final WebClient.Builder builder,
      final MentalHealthConfig config,
      final LocationMapper locationMapper) {
    this.locationMapper = locationMapper;
    this.client =
        builder
            .baseUrl(config.getLocationBaseUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
  }

  public Location getLocationForIpAddress(final String ipAddress) {
    try {
      final var locationExtracted = fetchLocation(ipAddress);
      return getLocation(locationExtracted);
    } catch (final Exception e) {
      log.error("Error fetching location for IP address {}: {}", ipAddress, e.getMessage());
      return Location.getDefaultLocation();
    }
  }

  private LocationDTO fetchLocation(final String ipAddress) {
    return client
        .get()
        .uri(uriBuilder -> getUriBuilder(ipAddress, uriBuilder))
        .retrieve()
        .bodyToMono(LocationDTO.class)
        .block();
  }

  private URI getUriBuilder(final String ipAddress, final UriBuilder uriBuilder) {
    return uriBuilder.path(ipAddress).build();
  }

  private Location getLocation(final LocationDTO locationDTO) {
    return locationDTO.city() == null
        ? Location.getDefaultLocation()
        : locationMapper.from(locationDTO);
  }
}
