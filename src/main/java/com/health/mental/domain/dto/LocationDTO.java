package com.health.mental.domain.dto;

public record LocationDTO(
    String country,
    String countryCode,
    String region,
    String regionName,
    String city,
    String zip,
    Double lat,
    Double lon,
    String timezone,
    String isp,
    String org,
    String as) {}
