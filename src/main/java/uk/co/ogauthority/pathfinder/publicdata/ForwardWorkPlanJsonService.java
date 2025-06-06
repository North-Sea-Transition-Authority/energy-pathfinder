package uk.co.ogauthority.pathfinder.publicdata;

import com.google.common.collect.Streams;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.repository.project.ProjectOperatorRepository;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractRepository;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityFileLinkRepository;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityRepository;
import uk.co.ogauthority.pathfinder.repository.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderRepository;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

@Service
class ForwardWorkPlanJsonService {

  private final ProjectDetailsRepository projectDetailsRepository;
  private final ProjectOperatorRepository projectOperatorRepository;
  private final ForwardWorkPlanUpcomingTenderRepository forwardWorkPlanUpcomingTenderRepository;
  private final ForwardWorkPlanAwardedContractRepository forwardWorkPlanAwardedContractRepository;
  private final ForwardWorkPlanCollaborationOpportunityRepository forwardWorkPlanCollaborationOpportunityRepository;
  private final ForwardWorkPlanCollaborationOpportunityFileLinkRepository forwardWorkPlanCollaborationOpportunityFileLinkRepository;

  ForwardWorkPlanJsonService(
      ProjectDetailsRepository projectDetailsRepository,
      ProjectOperatorRepository projectOperatorRepository,
      ForwardWorkPlanUpcomingTenderRepository forwardWorkPlanUpcomingTenderRepository,
      ForwardWorkPlanAwardedContractRepository forwardWorkPlanAwardedContractRepository,
      ForwardWorkPlanCollaborationOpportunityRepository forwardWorkPlanCollaborationOpportunityRepository,
      ForwardWorkPlanCollaborationOpportunityFileLinkRepository forwardWorkPlanCollaborationOpportunityFileLinkRepository
  ) {
    this.projectDetailsRepository = projectDetailsRepository;
    this.projectOperatorRepository = projectOperatorRepository;
    this.forwardWorkPlanUpcomingTenderRepository = forwardWorkPlanUpcomingTenderRepository;
    this.forwardWorkPlanAwardedContractRepository = forwardWorkPlanAwardedContractRepository;
    this.forwardWorkPlanCollaborationOpportunityRepository = forwardWorkPlanCollaborationOpportunityRepository;
    this.forwardWorkPlanCollaborationOpportunityFileLinkRepository = forwardWorkPlanCollaborationOpportunityFileLinkRepository;
  }

  Set<ForwardWorkPlanJson> getPublishedForwardWorkPlans() {
    var allProjectDetails =
        projectDetailsRepository.getAllPublishedProjectDetailsByProjectTypes(EnumSet.of(ProjectType.FORWARD_WORK_PLAN));

    // TODO: When replatforming to use Postgres, switch to findAllByProjectDetail_IdIn. We can't do this with Oracle at the moment
    // due to the 1000 IN clause limit.
    var projectOperatorByProjectDetailId = Streams.stream(projectOperatorRepository.findAll())
        .collect(Collectors.toMap(projectOperator -> projectOperator.getProjectDetail().getId(), Function.identity()));

    var forwardWorkPlanUpcomingTendersByProjectDetailId = Streams.stream(forwardWorkPlanUpcomingTenderRepository.findAll())
        .collect(Collectors.groupingBy(forwardWorkPlanUpcomingTender -> forwardWorkPlanUpcomingTender.getProjectDetail().getId()));

    var forwardWorkPlanAwardedContractsByProjectDetailId = Streams.stream(forwardWorkPlanAwardedContractRepository.findAll())
        .collect(Collectors.groupingBy(forwardWorkPlanAwardedContract -> forwardWorkPlanAwardedContract.getProjectDetail().getId()));

    var fileLinkByForwardWorkPlanCollaborationOpportunityId =
        Streams.stream(forwardWorkPlanCollaborationOpportunityFileLinkRepository.findAll())
            .collect(Collectors.toMap(fileLink -> fileLink.getCollaborationOpportunity().getId(),Function.identity()));

    var forwardWorkPlanCollaborationOpportunityToFileLinkByProjectDetailId =
        Streams.stream(forwardWorkPlanCollaborationOpportunityRepository.findAll())
            .collect(
                Collectors.groupingBy(
                    collaborationOpportunity -> collaborationOpportunity.getProjectDetail().getId(),
                    StreamUtil.toMapNullValueFriendly(
                        Function.identity(),
                        collaborationOpportunity ->
                            fileLinkByForwardWorkPlanCollaborationOpportunityId.get(collaborationOpportunity.getId())
                    )
                )
            );

    return allProjectDetails
        .stream()
        .map(projectDetail ->
            ForwardWorkPlanJson.from(
                projectDetail,
                projectOperatorByProjectDetailId.get(projectDetail.getId()),
                forwardWorkPlanUpcomingTendersByProjectDetailId.get(projectDetail.getId()),
                forwardWorkPlanAwardedContractsByProjectDetailId.get(projectDetail.getId()),
                forwardWorkPlanCollaborationOpportunityToFileLinkByProjectDetailId.get(projectDetail.getId())
            )
        )
        .collect(Collectors.toSet());
  }
}
