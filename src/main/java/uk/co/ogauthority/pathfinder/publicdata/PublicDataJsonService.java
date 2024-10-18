package uk.co.ogauthority.pathfinder.publicdata;

import org.springframework.stereotype.Service;

@Service
class PublicDataJsonService {

  private final InfrastructureProjectJsonService infrastructureProjectJsonService;

  PublicDataJsonService(InfrastructureProjectJsonService infrastructureProjectJsonService) {
    this.infrastructureProjectJsonService = infrastructureProjectJsonService;
  }

  PublicDataJson getPublicDataJson() {
    return new PublicDataJson(infrastructureProjectJsonService.getPublishedInfrastructureProjects());
  }
}
