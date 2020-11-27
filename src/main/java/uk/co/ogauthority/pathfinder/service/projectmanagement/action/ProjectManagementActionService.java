package uk.co.ogauthority.pathfinder.service.projectmanagement.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.form.useraction.UserActionWithDisplayOrder;
import uk.co.ogauthority.pathfinder.service.team.ManageTeamService;

@Service
public class ProjectManagementActionService {

  private final ManageTeamService manageTeamService;
  private final RegulatorActionService regulatorActionService;

  @Autowired
  public ProjectManagementActionService(ManageTeamService manageTeamService,
                                        RegulatorActionService regulatorActionService) {
    this.manageTeamService = manageTeamService;
    this.regulatorActionService = regulatorActionService;
  }

  public List<UserActionWithDisplayOrder> getActions(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    List<UserActionWithDisplayOrder> actions;

    if (manageTeamService.isPersonMemberOfRegulatorTeam(user)) {
      actions = regulatorActionService.getActions(projectDetail, user);
    } else {
      return Collections.emptyList();
    }

    actions.sort(Comparator.comparing(UserActionWithDisplayOrder::getDisplayOrder));

    return actions;
  }
}
