package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.enums.Quarter;

class QuarterYearJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private String quarter = Quarter.Q1.name();
    private Integer year = 2025;

    private Builder() {
    }

    Builder withQuarter(String quarter) {
      this.quarter = quarter;
      return this;
    }

    Builder withYear(Integer year) {
      this.year = 2025;
      return this;
    }

    QuarterYearJson build() {
      return new QuarterYearJson(quarter, year);
    }
  }
}
