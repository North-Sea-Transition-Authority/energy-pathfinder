package uk.co.ogauthority.pathfinder.publicdata;

record CoordinateJson(
    Integer degrees,
    Integer minutes,
    Double seconds,
    String hemisphere
) {

  static CoordinateJson from(Integer degrees, Integer minutes, Double seconds, String hemisphere) {
    if (degrees == null && minutes == null && seconds == null && hemisphere == null) {
      return null;
    }

    return new CoordinateJson(degrees, minutes, seconds, hemisphere);
  }
}
