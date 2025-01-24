package uk.co.ogauthority.pathfinder.publicdata;

import java.time.LocalDateTime;

class ForwardWorkPlanJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private Integer id = 1;
    private ForwardWorkPlanDetailsJson details = ForwardWorkPlanDetailsJsonTestUtil.newBuilder().build();
    private LocalDateTime submittedOn = LocalDateTime.of(2024, 10, 29, 11, 20, 38, 424521789);

    private Builder() {
    }

    Builder withId(Integer id) {
      this.id = id;
      return this;
    }

    Builder withDetails(ForwardWorkPlanDetailsJson details) {
      this.details = details;
      return this;
    }

    Builder withSubmittedOn(LocalDateTime submittedOn) {
      this.submittedOn = submittedOn;
      return this;
    }

    ForwardWorkPlanJson build() {
      return new ForwardWorkPlanJson(
          id,
          details,
          submittedOn
      );
    }
  }
}
