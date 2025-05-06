package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.enums.Quarter;

record QuarterYearJson(
    String quarter,
    Integer year
) {

  static QuarterYearJson from(Quarter quarter, Integer year) {
    return new QuarterYearJson(
        quarter.name(),
        year
    );
  }
}
