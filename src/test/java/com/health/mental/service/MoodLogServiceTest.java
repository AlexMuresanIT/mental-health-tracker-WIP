package com.health.mental.service;

import static com.health.mental.TestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.health.mental.domain.MoodLog;
import com.health.mental.repository.MoodLogRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MoodLogServiceTest {

  @Mock private MoodLogRepository moodLogRepository;

  @Mock private UserService userService;

  @Mock private LocationService locationService;

  @Mock private IpAddressService ipAddressService;

  private MoodLogService moodLogService;

  @Captor ArgumentCaptor<MoodLog> moodLogCaptor;

  @BeforeEach
  void setUp() {
    moodLogService =
        new MoodLogService(moodLogRepository, userService, locationService, ipAddressService);
  }

  @Test
  void shouldAddMoodLogForUser() {
    final var moodLog = getMoodLog();
    when(userService.findById(any())).thenReturn(Optional.of(getUser("pw")));
    when(ipAddressService.fetchIpAddress()).thenReturn(IP_ADDRESS);
    when(locationService.getLocationForIpAddress(any())).thenReturn(getLocation());

    moodLogService.addMoodLogForUser(ID1, moodLog);

    verify(moodLogRepository).save(moodLogCaptor.capture());

    final var savedMoodLog = moodLogCaptor.getValue();
    assertThat(savedMoodLog).isNotNull();
    assertThat(savedMoodLog.mood()).isEqualTo(moodLog.mood());
    assertThat(savedMoodLog.id()).isNull();
    assertThat(savedMoodLog.userId()).isEqualTo(moodLog.userId());
    assertThat(savedMoodLog.createdAt()).isNotNull();

    verify(userService).saveMoodLogForUser(any(), any());
  }
}
