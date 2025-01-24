package uk.co.ogauthority.pathfinder.publicdata;

import com.google.common.collect.Streams;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.repository.project.ProjectOperatorRepository;

@Service
class ForwardWorkPlanJsonService {

  private final ProjectDetailsRepository projectDetailsRepository;
  private final ProjectOperatorRepository projectOperatorRepository;

  ForwardWorkPlanJsonService(
      ProjectDetailsRepository projectDetailsRepository,
      ProjectOperatorRepository projectOperatorRepository
  ) {
    this.projectDetailsRepository = projectDetailsRepository;
    this.projectOperatorRepository = projectOperatorRepository;
  }

  Set<ForwardWorkPlanJson> getPublishedForwardWorkPlans() {
    var allProjectDetails = projectDetailsRepository.getAllPublishedProjectDetailsByProjectType(ProjectType.FORWARD_WORK_PLAN);

    // TODO: When replatforming to use Postgres, switch to findAllByProjectDetail_IdIn. We can't do this with Oracle at the moment
    // due to the 1000 IN clause limit.
    var projectOperatorByProjectDetailId = Streams.stream(projectOperatorRepository.findAll())
        .collect(Collectors.toMap(projectOperator -> projectOperator.getProjectDetail().getId(), Function.identity()));

    return allProjectDetails
        .stream()
        .map(projectDetail ->
            ForwardWorkPlanJson.from(
                projectDetail,
                projectOperatorByProjectDetailId.get(projectDetail.getId())
            )
        )
        .collect(Collectors.toSet());
  }
}
