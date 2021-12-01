package uk.co.ogauthority.pathfinder.controller.project.start.infrastructure;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.config.MetricsProvider;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.selectoperator.SelectProjectOperatorController;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.ProjectOperatorForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.StartProjectService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;

@Controller
@RequestMapping("/infrastructure/start-project")
public class InfrastructureProjectStartController {

  private final StartProjectService startProjectService;
  private final TeamService teamService;
  private final MetricsProvider metricsProvider;

  @Autowired
  public InfrastructureProjectStartController(StartProjectService startProjectService,
                                              TeamService teamService,
                                              MetricsProvider metricsProvider) {
    this.startProjectService = startProjectService;
    this.teamService = teamService;
    this.metricsProvider = metricsProvider;
  }

  @GetMapping
  public ModelAndView startPage(AuthenticatedUserAccount user) {
    return new ModelAndView("project/start/infrastructure/infrastructureStartPage")
        .addObject(
            "startActionUrl",
            ReverseRouter.route(on(InfrastructureProjectStartController.class).startProject(null))
        )
        .addObject(
            "infrastructureProjectTypeLowercaseDisplayName",
            ProjectType.INFRASTRUCTURE.getLowercaseDisplayName()
        );
  }

  /**
   * If a user is in multiple teams direct them to a page to choose which team the project is for.
   * If they are in a single team create a project for that team.
   * @param user the user creating the project.
   * @return task list or the select a team page.
   */
  @PostMapping
  public ModelAndView startProject(AuthenticatedUserAccount user) {
    //if in multiple teams redirect to the team select
    final var organisationTeams = teamService.getOrganisationTeamsPersonIsMemberOf(user.getLinkedPerson());

    if (organisationTeams.size() > 1) {
      return ReverseRouter.redirect(on(SelectProjectOperatorController.class).selectOperator(null));
    }

    //User is in one team so start project
    final var projectOperatorForm = new ProjectOperatorForm();
    projectOperatorForm.setOperator(String.valueOf(organisationTeams.get(0).getPortalOrganisationGroup().getOrgGrpId()));

    final var projectDetail = startProjectService.createInfrastructureProject(user, projectOperatorForm);
    metricsProvider.getProjectStartCounter().increment();
    return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectDetail.getProject().getId(), null));
  }
}
