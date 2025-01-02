package uk.co.ogauthority.pathfinder.publicdata;

record StartEndYearJson(Integer startYear, Integer endYear) {

  static StartEndYearJson from(String startYear, String endYear) {
    return new StartEndYearJson(
        Integer.valueOf(startYear),
        Integer.valueOf(endYear)
    );
  }
}
