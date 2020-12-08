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
import uk.co.ogauthority.pathfinder.service.team.TeamService;

@Service
public class ProjectManagementActionService {

  private final ManageTeamService manageTeamService;
  private final TeamService teamService;
  private final RegulatorActionService regulatorActionService;
  private final OperatorActionService operatorActionService;

  @Autowired
  public ProjectManagementActionService(ManageTeamService manageTeamService,
                                        TeamService teamService,
                                        RegulatorActionService regulatorActionService,
                                        OperatorActionService operatorActionService) {
    this.manageTeamService = manageTeamService;
    this.teamService = teamService;
    this.regulatorActionService = regulatorActionService;
    this.operatorActionService = operatorActionService;
  }

  public List<UserActionWithDisplayOrder> getActions(ProjectDetail projectDetail, AuthenticatedUserAccount user) {
    List<UserActionWithDisplayOrder> actions;

    if (manageTeamService.isPersonMemberOfRegulatorTeam(user)) {
      actions = regulatorActionService.getActions(projectDetail, user);
    } else if (teamService.isPersonMemberOfAnyOrganisationTeam(user)) {
      actions = operatorActionService.getActions(projectDetail, user);
    } else {
      return Collections.emptyList();
    }

    actions.sort(Comparator.comparing(UserActionWithDisplayOrder::getDisplayOrder));

    return actions;
  }
}
