package com.health.mental;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.common.ContentTypes.CONTENT_TYPE;
import static com.health.mental.domain.Gender.MALE;
import static com.health.mental.domain.MoodTag.PRODUCTIVITY;
import static com.health.mental.domain.MoodType.CALM;

import com.health.generated.model.MoodLogRequest;
import com.health.mental.domain.*;
import com.health.mental.domain.dto.MoodLogDTO;
import com.health.mental.domain.dto.UserDTO;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.http.MediaType;

public class TestUtil {

  public static final String ID1 = "1";
  private static final String NAME = "John Doe";
  private static final String EMAIL = "email@yahoo.com";
  public static final String PASSWORD = "password123";
  public static final String IP_ADDRESS = "127.0.0.1";
  private static final String CONTENT_TYPE = "Content-Type";

  public static User getUser() {
    return getUser(PASSWORD);
  }

  public static User getUser(final String password) {
    return new User(ID1, NAME, EMAIL, MALE, 30, password, List.of(getMoodLog()));
  }

  public static UserDTO getUserDTO() {
    return new UserDTO(NAME, EMAIL, MALE, 30, PASSWORD, List.of(getMoodLogDTO()));
  }

  public static MoodLog getMoodLog() {
    return new MoodLog(
        ID1, ID1, CALM, "Feeling good", 5, Set.of(PRODUCTIVITY), getLocation(), null);
  }

  public static MoodLogDTO getMoodLogDTO() {
    return new MoodLogDTO(CALM, "Feeling good", 5, Set.of(PRODUCTIVITY), getLocation().city());
  }

  public static Location getLocation() {
    return new Location("RO", "CJ", 89.90, 123.23, "EU/RO");
  }

  public static MoodLogRequest createMoodLogRequest() {
    final var request = new MoodLogRequest();
    request.setMood(com.health.generated.model.MoodType.BORED);
    request.setIntensity(4);
    request.setNotes("Notes");
    request.setTags(
        Set.of(
            com.health.generated.model.MoodTag.PRODUCTIVITY,
            com.health.generated.model.MoodTag.FRIENDS));
    return request;
  }

  public static void initializeURL(final String url, final String testDataPath) throws IOException {
    final var stream =
        Objects.requireNonNull(TestUtil.class.getClassLoader().getResourceAsStream(testDataPath));
    stubFor(
        get(urlMatching(url))
            .willReturn(
                aResponse()
                    .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(stream.readAllBytes())));
  }
}
