package com.health.mental.service;

import com.health.mental.domain.MoodLog;
import com.health.mental.repository.MoodLogRepository;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MoodLogService {

  private final MoodLogRepository moodLogRepository;
  private final UserService userService;
  private final LocationService locationService;
  private final IpAddressService ipAddressService;

  public MoodLogService(
      final MoodLogRepository moodLogRepository,
      final UserService userService,
      final LocationService locationService,
      final IpAddressService ipAddressService) {
    this.moodLogRepository = moodLogRepository;
    this.userService = userService;
    this.locationService = locationService;
    this.ipAddressService = ipAddressService;
  }

  public void addMoodLogForUser(final String userId, final MoodLog moodLog) {
    final var maybeUser = userService.findById(userId);
    maybeUser.ifPresent(
        user -> {
          final var ipAddress = ipAddressService.fetchIpAddress();
          final var location = locationService.getLocationForIpAddress(ipAddress);
          final var createdAt = OffsetDateTime.now();
          final var enhancedMoodLog = MoodLog.enhanceMoodLog(userId, moodLog, location, createdAt);
          moodLogRepository.save(enhancedMoodLog);
          userService.saveMoodLogForUser(user, enhancedMoodLog);
        });
  }

  public List<MoodLog> getAllMoodLogsForUserId(final String userId) {
    return moodLogRepository.findAllByUserId(userId);
  }
}
