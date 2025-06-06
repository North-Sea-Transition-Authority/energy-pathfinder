package uk.co.ogauthority.pathfinder.util;

import com.google.common.collect.Lists;
import java.util.List;

/**
 * Util class to set up pagination for queries that could hit the oracle limit of 1000 items passed to the IN clause.
 */
public class OraclePartitionUtil {

  public static final int ORACLE_LIMIT = 1000;

  private OraclePartitionUtil() {
    throw new IllegalStateException("OraclePartitionUtil is a utility class and should not be instantiated");
  }

  /**
   * returns a partitioned list of items using the oracle limit.
   * @param list The full list of items which will be partitioned in chunks of 1000 and the remainder
   * @return The partitioned list of items
   */
  public static <T> List<List<T>> partitionedList(List<T> list) {
    return Lists.partition(list, ORACLE_LIMIT);
  }
}
