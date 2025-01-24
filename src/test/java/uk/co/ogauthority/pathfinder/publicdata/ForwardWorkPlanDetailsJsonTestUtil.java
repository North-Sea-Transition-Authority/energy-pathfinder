package uk.co.ogauthority.pathfinder.publicdata;

class ForwardWorkPlanDetailsJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private String operatorName = "BP";

    private Builder() {
    }

    Builder withOperatorName(String operatorName) {
      this.operatorName = operatorName;
      return this;
    }

    ForwardWorkPlanDetailsJson build() {
      return new ForwardWorkPlanDetailsJson(operatorName);
    }
  }
}
