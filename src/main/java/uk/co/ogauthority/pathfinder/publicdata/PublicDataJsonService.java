package uk.co.ogauthority.pathfinder.publicdata;

import org.springframework.stereotype.Service;

@Service
class PublicDataJsonService {

  private final InfrastructureProjectJsonService infrastructureProjectJsonService;
  private final ForwardWorkPlanJsonService forwardWorkPlanJsonService;

  PublicDataJsonService(
      InfrastructureProjectJsonService infrastructureProjectJsonService,
      ForwardWorkPlanJsonService forwardWorkPlanJsonService
  ) {
    this.infrastructureProjectJsonService = infrastructureProjectJsonService;
    this.forwardWorkPlanJsonService = forwardWorkPlanJsonService;
  }

  PublicDataJson getPublicDataJson() {
    var infrastructureProjects = infrastructureProjectJsonService.getPublishedInfrastructureProjects();
    var forwardWorkPlans = forwardWorkPlanJsonService.getPublishedForwardWorkPlans();

    return new PublicDataJson(infrastructureProjects, forwardWorkPlans);
  }
}
