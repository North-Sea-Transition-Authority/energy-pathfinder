package uk.co.ogauthority.pathfinder.model.licenceblock;

import uk.co.ogauthority.pathfinder.util.LicenceBlockUtil;

public interface SortableLicenceBlock {

  String getQuadrantNumber();

  String getBlockNumber();

  String getBlockSuffix();

  default String getSortKey() {
    return LicenceBlockUtil.getSortKey(getQuadrantNumber(), getBlockNumber(), getBlockSuffix());
  }
}
