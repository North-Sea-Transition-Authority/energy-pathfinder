package uk.co.ogauthority.pathfinder.testutil;

import java.util.List;
import java.util.stream.Collectors;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentSchedule;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentWell;
import uk.co.ogauthority.pathfinder.model.entity.wellbore.Wellbore;

public class PlugAbandonmentWellTestUtil {

  private PlugAbandonmentWellTestUtil() {
    throw new IllegalStateException("PlugAbandonmentWellTestUtil is a utility class and should not be instantiated");
  }

  public static List<PlugAbandonmentWell> getUnorderedPlugAbandonmentWells() {
    return WellboreTestUtil.getUnorderedWellbores()
        .stream()
        .map(PlugAbandonmentWellTestUtil::createPlugAbandonmentWell)
        .collect(Collectors.toList());
  }

  public static List<PlugAbandonmentWell> getOrderedPlugAbandonmentWells() {
    return WellboreTestUtil.getOrderedWellbores()
        .stream()
        .map(PlugAbandonmentWellTestUtil::createPlugAbandonmentWell)
        .collect(Collectors.toList());
  }

  public static PlugAbandonmentWell createPlugAbandonmentWell() {
    return createPlugAbandonmentWell(WellboreTestUtil.createWellbore());
  }

  public static PlugAbandonmentWell createPlugAbandonmentWell(Integer id, PlugAbandonmentSchedule plugAbandonmentSchedule) {
    return createPlugAbandonmentWell(id, plugAbandonmentSchedule, WellboreTestUtil.createWellbore());
  }

  public static PlugAbandonmentWell createPlugAbandonmentWell(Wellbore wellbore) {
    return createPlugAbandonmentWell(null, PlugAbandonmentScheduleTestUtil.createPlugAbandonmentSchedule(), wellbore);
  }

  public static PlugAbandonmentWell createPlugAbandonmentWell(
      Integer id,
      PlugAbandonmentSchedule plugAbandonmentSchedule,
      Wellbore wellbore
  ) {
    var plugAbandonmentWell = new PlugAbandonmentWell(id);
    plugAbandonmentWell.setPlugAbandonmentSchedule(plugAbandonmentSchedule);
    plugAbandonmentWell.setWellbore(wellbore);
    return plugAbandonmentWell;
  }
}
