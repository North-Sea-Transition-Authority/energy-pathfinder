package uk.co.ogauthority.pathfinder.publicdata;

import java.util.Set;

class InfrastructureProjectCampaignJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private String scope = "Test scope";
    private Boolean partOfExistingInfrastructureProjectCampaign = true;
    private Set<Integer> existingCampaignInfrastructureProjectIds = Set.of(1, 2);

    private Builder() {
    }

    Builder withScope(String scope) {
      this.scope = scope;
      return this;
    }

    Builder withPartOfExistingInfrastructureProjectCampaign(Boolean partOfExistingInfrastructureProjectCampaign) {
      this.partOfExistingInfrastructureProjectCampaign = partOfExistingInfrastructureProjectCampaign;
      return this;
    }

    Builder withExistingCampaignInfrastructureProjectIds(Set<Integer> existingCampaignInfrastructureProjectIds) {
      this.existingCampaignInfrastructureProjectIds = existingCampaignInfrastructureProjectIds;
      return this;
    }

    InfrastructureProjectCampaignJson build() {
      return new InfrastructureProjectCampaignJson(
          scope,
          partOfExistingInfrastructureProjectCampaign,
          existingCampaignInfrastructureProjectIds
      );
    }
  }
}
