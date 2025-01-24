package uk.co.ogauthority.pathfinder.publicdata;

import java.util.Set;

record PublicDataJson(
    Set<InfrastructureProjectJson> infrastructureProjects,
    Set<ForwardWorkPlanJson> forwardWorkPlans
) { }
