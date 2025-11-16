package com.health.mental;

import static com.health.mental.domain.Gender.MALE;
import static com.health.mental.domain.MoodTag.PRODUCTIVITY;
import static com.health.mental.domain.MoodType.CALM;

import com.health.mental.domain.*;
import com.health.mental.domain.dto.MoodLogDTO;
import com.health.mental.domain.dto.UserDTO;
import java.util.List;
import java.util.Set;

public class TestUtil {

  public static final String ID1 = "1";
  private static final String NAME = "John Doe";
  private static final String EMAIL = "email@yahoo.com";
  public static final String PASSWORD = "password123";
  public static final String IP_ADDRESS = "127.0.0.1";

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
}
