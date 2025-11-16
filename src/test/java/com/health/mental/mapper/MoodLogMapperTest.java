package com.health.mental.mapper;

import static com.health.mental.TestUtil.createMoodLogRequest;
import static com.health.mental.TestUtil.getMoodLog;
import static org.assertj.core.api.Assertions.assertThat;

import com.health.generated.model.MoodTag;
import org.junit.jupiter.api.Test;

public class MoodLogMapperTest {
  private static final String GOOGLE_MAPS_LINK = "https://www.google.com/maps/place/%s,%s";

  private final MoodLogMapper moodLogMapper = new MoodLogMapperImpl();

  @Test
  void testFromRequest() {
    final var request = createMoodLogRequest();
    final var domainMood = moodLogMapper.fromDTO(request);

    assertThat(domainMood).isNotNull();
    assertThat(domainMood.mood()).isEqualTo(com.health.mental.domain.MoodType.BORED);
    assertThat(domainMood.intensity()).isEqualTo(request.getIntensity());
    assertThat(domainMood.notes()).isEqualTo(request.getNotes());
    assertThat(domainMood.tags())
        .containsExactlyInAnyOrder(
            com.health.mental.domain.MoodTag.PRODUCTIVITY,
            com.health.mental.domain.MoodTag.FRIENDS);
    assertThat(domainMood.id()).isNull();
    assertThat(domainMood.userId()).isNull();
  }

  @Test
  void testToResponse() {
    final var domainMood = getMoodLog();
    final var response = moodLogMapper.toResponse(domainMood);

    assertThat(response).isNotNull();
    assertThat(response.getMood()).isEqualTo(com.health.generated.model.MoodType.CALM);
    assertThat(response.getIntensity()).isEqualTo(domainMood.intensity());
    assertThat(response.getNotes()).isEqualTo(domainMood.notes());
    assertThat(response.getTags()).containsExactlyInAnyOrder(MoodTag.PRODUCTIVITY);

    final var locationDTO = response.getLocation();
    assertThat(locationDTO).isNotNull();
    assertThat(locationDTO.getCity()).isEqualTo(domainMood.location().city());
    assertThat(locationDTO.getCountry()).isEqualTo(domainMood.location().country());
    assertThat(locationDTO.getLocationLink())
        .isEqualTo(
            GOOGLE_MAPS_LINK.formatted(
                domainMood.location().latitude(), domainMood.location().longitude()));
  }
}
