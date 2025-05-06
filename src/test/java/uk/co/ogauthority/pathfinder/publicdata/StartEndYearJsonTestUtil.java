package uk.co.ogauthority.pathfinder.publicdata;

class StartEndYearJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private Integer startYear = 2024;
    private Integer endYear = 2025;

    private Builder() {
    }

    Builder withStartYear(Integer startYear) {
      this.startYear = startYear;
      return this;
    }

    Builder withEndYear(Integer endYear) {
      this.endYear = endYear;
      return this;
    }

    StartEndYearJson build() {
      return new StartEndYearJson(startYear, endYear);
    }
  }
}
