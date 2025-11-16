package com.health.mental.controller;

import com.health.generated.api.MoodLogApi;
import com.health.generated.model.MoodLogRequest;
import com.health.generated.model.MoodLogResponse;
import com.health.mental.config.security.SecurityUtils;
import com.health.mental.mapper.MoodLogMapper;
import com.health.mental.service.MoodLogService;
import io.micrometer.common.util.StringUtils;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MoodLogController implements MoodLogApi {

  private final MoodLogService moodLogService;
  private final MoodLogMapper moodLogMapper;

  public MoodLogController(final MoodLogService moodLogService, final MoodLogMapper moodLogMapper) {
    this.moodLogService = moodLogService;
    this.moodLogMapper = moodLogMapper;
  }

  @Override
  public ResponseEntity<Void> addMoodLogForUserId(
      final String userId, final MoodLogRequest moodLogRequest) {
    if (!isValidMood(moodLogRequest)) {
      return ResponseEntity.badRequest().build();
    }
    final var userIpAddress = SecurityUtils.getUserIpAddress(null);
    final var domainMoodLog = moodLogMapper.fromDTO(moodLogRequest);
    moodLogService.addMoodLogForUser(userId, userIpAddress, domainMoodLog);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<List<MoodLogResponse>> getAllMoodLogsForUserId(final String userId) {
    if (StringUtils.isBlank(userId)) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok(
        moodLogMapper.toResponse(moodLogService.getAllMoodLogsForUserId(userId)));
  }

  private static boolean isValidMood(final MoodLogRequest request) {
    return request.getMood() != null
        && request.getNotes() != null
        && request.getIntensity() != null;
  }
}
