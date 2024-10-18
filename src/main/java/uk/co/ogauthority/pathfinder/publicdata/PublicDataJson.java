package uk.co.ogauthority.pathfinder.publicdata;

import java.util.List;

record PublicDataJson(
    List<InfrastructureProjectJson> infrastructureProjects
) { }
