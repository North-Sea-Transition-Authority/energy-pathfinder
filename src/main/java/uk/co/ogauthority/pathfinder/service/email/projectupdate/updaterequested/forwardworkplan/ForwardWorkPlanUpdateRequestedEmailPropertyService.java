package uk.co.ogauthority.pathfinder.service.email.projectupdate.updaterequested.forwardworkplan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.requested.ProjectUpdateRequestedEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.requested.forwardworkplan.ForwardWorkPlanUpdateRequestedEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.LinkService;
import uk.co.ogauthority.pathfinder.service.email.projectupdate.updaterequested.UpdateRequestedEmailPropertyProvider;

@Service
class ForwardWorkPlanUpdateRequestedEmailPropertyService implements UpdateRequestedEmailPropertyProvider {

  private final LinkService linkService;

  private final ServiceProperties serviceProperties;

  @Autowired
  ForwardWorkPlanUpdateRequestedEmailPropertyService(LinkService linkService,
                                                     ServiceProperties serviceProperties) {
    this.linkService = linkService;
    this.serviceProperties = serviceProperties;
  }

  @Override
  public ProjectType getSupportedProjectType() {
    return ProjectType.FORWARD_WORK_PLAN;
  }

  @Override
  public ProjectUpdateRequestedEmailProperties getUpdateRequestedEmailProperties(ProjectDetail projectDetail,
                                                                                 String updateReason,
                                                                                 String deadlineDate) {
    return new ForwardWorkPlanUpdateRequestedEmailProperties(
        updateReason,
        deadlineDate,
        linkService.generateProjectManagementUrl(projectDetail.getProject()),
        serviceProperties.getCustomerMnemonic()
    );
  }
}
