package uk.co.ogauthority.pathfinder.publicdata;

class CoordinateJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private Integer degrees = 51;
    private Integer minutes = 30;
    private Double seconds = 32.4;
    private String hemisphere = "NORTH";

    private Builder() {
    }

    Builder withDegrees(Integer degrees) {
      this.degrees = degrees;
      return this;
    }

    Builder withMinutes(Integer minutes) {
      this.minutes = minutes;
      return this;
    }

    Builder withSeconds(Double seconds) {
      this.seconds = seconds;
      return this;
    }

    Builder withHemisphere(String hemisphere) {
      this.hemisphere = hemisphere;
      return this;
    }

    CoordinateJson build() {
      return new CoordinateJson(degrees, minutes, seconds, hemisphere);
    }
  }
}
