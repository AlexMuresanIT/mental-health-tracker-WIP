package com.health.mental.controller;

import static com.health.mental.TestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.health.generated.model.MoodLogRequest;
import com.health.generated.model.MoodLogResponse;
import com.health.generated.model.MoodTag;
import com.health.mental.domain.MoodType;
import com.health.mental.repository.MoodLogRepository;
import com.health.mental.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
public class MoodLogControllerTest {
  private static final String GOOGLE_MAPS_LINK = "https://www.google.com/maps/place/%s,%s";

  private static final String MOOD_LOG_FOR_USER_ID = "/api/mood-log/{userId}";

  private static final String SPRING_DATA_MONGODB_URI = "spring.data.mongodb.uri";

  @Container
  public static MongoDBContainer mongoDBContainer =
      new MongoDBContainer(DockerImageName.parse("mongo:latest")).withExposedPorts(27017);

  static {
    mongoDBContainer.start();
  }

  @DynamicPropertySource
  static void configureMongoDB(final DynamicPropertyRegistry registry) {
    registry.add(SPRING_DATA_MONGODB_URI, mongoDBContainer::getReplicaSetUrl);
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private MoodLogRepository moodLogRepository;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserRepository userRepository;

  @Test
  void shouldAddMoodLogForUserId() throws Exception {
    final var user = getUser();
    final var request = createMoodLogRequest();

    userRepository.save(user);
    moodLogRepository.saveAll(user.userMoodLogs());

    assertThat(user.userMoodLogs()).hasSize(1);

    mockMvc
        .perform(
            post(MOOD_LOG_FOR_USER_ID, ID1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    final var updatedUser = userRepository.findById(ID1).orElseThrow();
    assertThat(updatedUser.userMoodLogs()).hasSize(2);

    final var moodLogs = moodLogRepository.findAllByUserId(ID1);
    assertThat(moodLogs).hasSize(2);

    final var latestMoodLog = moodLogs.getLast();
    assertThat(latestMoodLog.mood()).isEqualTo(MoodType.BORED);
    assertThat(latestMoodLog.userId()).isEqualTo(ID1);
  }

  @Test
  void shouldReturnBadRequest() throws Exception {
    final var request = new MoodLogRequest();

    mockMvc
        .perform(
            post(MOOD_LOG_FOR_USER_ID, ID1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnAllMoodLogsForUserId() throws Exception {
    final var user = getUser();
    final var moodLogs = user.userMoodLogs();
    userRepository.save(user);
    moodLogRepository.saveAll(moodLogs);

    final var responseAsString =
        mockMvc
            .perform(
                get(MOOD_LOG_FOR_USER_ID, ID1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    final var response =
        objectMapper.readValue(responseAsString, new TypeReference<List<MoodLogResponse>>() {});

    assertThat(response).isNotNull();
    assertThat(response).hasSize(1);

    final var moodLog = response.get(0);
    assertThat(moodLog.getUserId()).isEqualTo(ID1);
    assertThat(moodLog.getMood()).isEqualTo(com.health.generated.model.MoodType.CALM);
    assertThat(moodLog.getIntensity()).isEqualTo(5);
    assertThat(moodLog.getTags()).containsExactlyInAnyOrder(MoodTag.PRODUCTIVITY);

    final var location = moodLog.getLocation();
    assertThat(location).isNotNull();
    assertThat(location.getCountry()).isEqualTo("RO");
    assertThat(location.getCity()).isEqualTo("CJ");
    assertThat(location.getLocationLink())
        .isEqualTo(
            GOOGLE_MAPS_LINK.formatted(
                moodLogs.getFirst().location().latitude(),
                moodLogs.getFirst().location().longitude()));
  }

  @Test
  void shouldReturnBadRequestForInvalidUserId() throws Exception {
    final var invalidUserId = "   ";

    mockMvc
        .perform(
            get(MOOD_LOG_FOR_USER_ID, invalidUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @AfterEach
  void afterEach() {
    moodLogRepository.deleteAll();
    userRepository.deleteAll();
  }
}
