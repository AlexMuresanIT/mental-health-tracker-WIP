package com.health.mental.mapper;

import com.health.generated.model.Location;
import com.health.generated.model.MoodLogRequest;
import com.health.generated.model.MoodLogResponse;
import com.health.mental.domain.MoodLog;
import com.health.mental.domain.dto.MoodLogDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface MoodLogMapper {
  String GOOGLE_MAPS_LINK = "https://www.google.com/maps/place/%s,%s";

  @Mapping(target = "city", source = "moodLog.location.city")
  MoodLogDTO from(MoodLog moodLog);

  MoodLog to(MoodLogDTO moodLogDTO);

  List<MoodLogDTO> from(List<MoodLog> moodLogs);

  List<MoodLog> to(List<MoodLogDTO> moodLogDTOs);

  MoodLog fromDTO(MoodLogRequest moodLogRequest);

  MoodLogResponse toResponse(MoodLog moodLog);

  @Mapping(
      target = "locationLink",
      expression = "java(mapLatAndLong(location.latitude(), location.longitude()))")
  Location toDTO(com.health.mental.domain.Location location);

  List<MoodLogResponse> toResponse(List<MoodLog> moodLogs);

  default String mapLatAndLong(final Double latitude, final Double longitude) {
    if (latitude == null || longitude == null) {
      return null;
    }
    return GOOGLE_MAPS_LINK.formatted(latitude, longitude);
  }
}
