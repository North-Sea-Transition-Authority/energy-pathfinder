package uk.co.ogauthority.pathfinder.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.List;
import org.junit.jupiter.api.Test;

class StreamUtilTest {

  @Test
  void toMapNullValueFriendly_notNullValues() {
    var input = List.of("apple", "orange");

    var result = input
        .stream()
        .collect(StreamUtil.toMapNullValueFriendly(s -> s, String::length));

    assertThat(result).containsOnly(
        entry("apple", 5),
        entry("orange", 6)
    );
  }

  @Test
  void toMapNullValueFriendly_nullValues() {
    var input = List.of("apple", "orange");

    var result = input
        .stream()
        .collect(StreamUtil.toMapNullValueFriendly(s -> s, s -> null));

    assertThat(result).containsOnly(
        entry("apple", null),
        entry("orange", null)
    );
  }

  @Test
  void toMapNullValueFriendly_duplicateKey() {
    var input = List.of("apple", "apple");

    var result = input
        .stream()
        .collect(StreamUtil.toMapNullValueFriendly(s -> s, String::length));

    assertThat(result).containsOnly(
        entry("apple", 5)
    );
  }
}
