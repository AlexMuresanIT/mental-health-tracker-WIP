package com.health.mental.domain;

import java.time.OffsetDateTime;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("moodLogs")
public record MoodLog(
    @Id String id,
    String userId,
    MoodType mood,
    String notes,
    Integer intensity,
    Set<MoodTag> tags,
    Location location,
    OffsetDateTime createdAt) {

  public static MoodLog enhanceMoodLog(
      final String userId,
      final MoodLog moodLog,
      final Location location,
      final OffsetDateTime createdAt) {
    return new MoodLog(
        null,
        userId,
        moodLog.mood(),
        moodLog.notes(),
        moodLog.intensity(),
        moodLog.tags(),
        location,
        createdAt);
  }
}
