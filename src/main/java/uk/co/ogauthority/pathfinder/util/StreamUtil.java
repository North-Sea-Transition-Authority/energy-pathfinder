package uk.co.ogauthority.pathfinder.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Helper class for common methods interacting with streams.
 */
public class StreamUtil {

  private StreamUtil() {
    throw new AssertionError();
  }

  public static <T, K, U> Collector<T, ?, Map<K, U>> toLinkedHashMap(Function<? super T, ? extends K> keyMapper,
                                                                     Function<? super T, ? extends U> valueMapper) {

    return Collectors.toMap(
        keyMapper,
        valueMapper,
        (u, v) -> {
          throw new IllegalStateException(String.format("Duplicate key %s", u));
        },
        LinkedHashMap::new
    );
  }

  public static <T, K, U> Collector<T, ?, Map<K,U>> toMapNullValueFriendly(
      Function<? super T, ? extends K> keyMapper,
      Function<? super T, ? extends U> valueMapper
  ) {
    return Collector.of(
        HashMap::new,
        (map, element) -> map.put(keyMapper.apply(element), valueMapper.apply(element)),
        (map1, map2) -> {
          map1.putAll(map2);
          return map1;
        }
    );
  }

  public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }

}
