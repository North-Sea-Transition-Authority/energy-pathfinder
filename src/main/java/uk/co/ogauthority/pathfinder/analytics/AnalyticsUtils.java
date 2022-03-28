package uk.co.ogauthority.pathfinder.analytics;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import uk.co.ogauthority.pathfinder.util.FormObjectMapper;

public class AnalyticsUtils {

  public static final String GA_CLIENT_ID_COOKIE_NAME = "pathfinder-ga-client-id";

  private AnalyticsUtils() {
    throw new IllegalStateException("AnalyticsUtils is a utility class and should not be instantiated");
  }

  public static Map<String, String> getFiltersUsedParamMap(Object searchParamsObject) {

    var filtersUsedMap = new HashMap<>(FormObjectMapper.toMap(searchParamsObject)).entrySet().stream()
        .filter(entry -> !StringUtils.isBlank(entry.getValue()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    var finalMap = new HashMap<String, String>();

    // replace filter value with a confirmation that the filter was used
    // de-duplicate list values by inserting keys stripped of array suffixes into fresh map
    filtersUsedMap.forEach((key, value) -> finalMap.put(key.replaceAll("\\[.*]", ""), "true"));

    return finalMap;

  }

}
