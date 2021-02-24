package uk.co.ogauthority.pathfinder.model.wellbore;

import uk.co.ogauthority.pathfinder.util.WellboreUtil;

public interface SortableWellbore {

  String getQuadrantNumber();

  String getBlockNumber();

  String getBlockSuffix();

  String getPlatformLetter();

  String getDrillingSeqNumber();

  String getWellSuffix();

  default String getSortKey() {
    return WellboreUtil.getSortKey(
        getQuadrantNumber(),
        getBlockNumber(),
        getBlockSuffix(),
        getPlatformLetter(),
        getDrillingSeqNumber(),
        getWellSuffix()
    );
  }
}
