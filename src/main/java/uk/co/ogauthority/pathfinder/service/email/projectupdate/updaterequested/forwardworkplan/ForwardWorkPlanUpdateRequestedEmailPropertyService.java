package uk.co.ogauthority.pathfinder.service.email.projectupdate.updaterequested.forwardworkplan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.requested.ProjectUpdateRequestedEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.requested.forwardworkplan.ForwardWorkPlanUpdateRequestedEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.email.EmailLinkService;
import uk.co.ogauthority.pathfinder.service.email.projectupdate.updaterequested.UpdateRequestedEmailPropertyProvider;

@Service
class ForwardWorkPlanUpdateRequestedEmailPropertyService implements UpdateRequestedEmailPropertyProvider {

  private final EmailLinkService emailLinkService;

  private final ServiceProperties serviceProperties;

  @Autowired
  ForwardWorkPlanUpdateRequestedEmailPropertyService(EmailLinkService emailLinkService,
                                                     ServiceProperties serviceProperties) {
    this.emailLinkService = emailLinkService;
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
        emailLinkService.generateProjectManagementUrl(projectDetail.getProject()),
        serviceProperties.getCustomerMnemonic()
    );
  }
}
