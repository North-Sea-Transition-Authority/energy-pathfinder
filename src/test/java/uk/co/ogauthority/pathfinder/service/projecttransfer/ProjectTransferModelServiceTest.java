package uk.co.ogauthority.pathfinder.service.projecttransfer;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.controller.projecttransfer.ProjectTransferController;
import uk.co.ogauthority.pathfinder.controller.rest.OrganisationGroupRestController;
import uk.co.ogauthority.pathfinder.controller.rest.OrganisationUnitRestController;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.form.projecttransfer.ProjectTransferForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectHeaderSummaryService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectTransferModelServiceTest {

  @Mock
  private SearchSelectorService searchSelectorService;

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  @Mock
  private ProjectOperatorService projectOperatorService;

  @Mock
  private ProjectHeaderSummaryService projectHeaderSummaryService;

  @Mock
  private BreadcrumbService breadcrumbService;

  private ProjectTransferModelService projectTransferModelService;

  @Before
  public void setup() {
    projectTransferModelService = new ProjectTransferModelService(
        searchSelectorService,
        portalOrganisationAccessor,
        projectOperatorService,
        projectHeaderSummaryService,
        breadcrumbService
    );
  }

  @Test
  public void getTransferProjectModelAndView_assertModelProperties() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var form = new ProjectTransferForm();
    final var projectId = projectDetail.getProject().getId();
    final var projectHeaderHtml = "html";
    final var projectOperator = ProjectOperatorTestUtil.getOperator();
    final var authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

    when(projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)).thenReturn(projectOperator);
    when(projectHeaderSummaryService.getProjectHeaderHtml(projectDetail, authenticatedUser)).thenReturn(projectHeaderHtml);

    var modelAndView = projectTransferModelService.getTransferProjectModelAndView(projectDetail, authenticatedUser, form);

    assertThat(modelAndView.getViewName()).isEqualTo(ProjectTransferModelService.TRANSFER_PROJECT_TEMPLATE_PATH);
    assertThat(modelAndView.getModelMap()).containsExactly(
        entry("projectHeaderHtml", projectHeaderHtml),
        entry("currentOperator", projectOperator.getOrganisationGroup().getName()),
        entry("form", form),
        entry("preselectedOperator", projectTransferModelService.getPreSelectedOrgGroup(form)),
        entry("operatorsRestUrl", SearchSelectorService.route(on(OrganisationGroupRestController.class)
            .searchPathfinderOrganisations(null))),
        entry("cancelUrl", ReverseRouter.route(on(ManageProjectController.class)
            .getProject(projectId, null, null, null))
        ),
        entry("organisationUnitRestUrl", SearchSelectorService.route(on(OrganisationUnitRestController.class)
            .searchOrganisationUnits(null))
        ),
        entry("preselectedPublishableOrganisation", projectTransferModelService.getPreSelectedPublishableOrganisation(form))
    );

    verify(breadcrumbService, times(1)).fromManageProject(projectId, modelAndView, ProjectTransferController.PAGE_NAME);
  }

  @Test
  public void getPreSelectedOrgGroup_whenFormValueIsNull_thenEmptyMap() {
    final var form = new ProjectTransferForm();
    final var result = projectTransferModelService.getPreSelectedOrgGroup(form);
    assertThat(result).isEmpty();
  }

  @Test
  public void getPreSelectedOrgGroup_whenFormValueFromList_thenEmptyMap() {

    final var organisationGroup = ProjectOperatorTestUtil.ORG_GROUP;

    final var organisationGroupId = String.valueOf(organisationGroup.getOrgGrpId());

    final var form = new ProjectTransferForm();
    form.setNewOrganisationGroup(organisationGroupId);

    when(searchSelectorService.buildPrePopulatedSelections(any(), any())).thenCallRealMethod();
    when(portalOrganisationAccessor.getOrganisationGroupOrError(organisationGroup.getOrgGrpId())).thenReturn(organisationGroup);

    final var result = projectTransferModelService.getPreSelectedOrgGroup(form);

    assertThat(result).containsExactly(
        entry(organisationGroupId, organisationGroup.getName())
    );
  }

  @Test
  public void getPreSelectedPublishableOrganisation_whenFormValueIsNull_thenEmptyMap() {
    final var form = new ProjectTransferForm();
    final var result = projectTransferModelService.getPreSelectedPublishableOrganisation(form);
    assertThat(result).isEmpty();
  }

  @Test
  public void getPreSelectedPublishableOrganisation_whenFormValueFromList_thenEmptyMap() {

    final var organisationUnit = TeamTestingUtil.generateOrganisationUnit(100, "name", new PortalOrganisationGroup());

    final var organisationUnitId = String.valueOf(organisationUnit.getOuId());

    final var form = new ProjectTransferForm();
    form.setPublishableOrganisation(organisationUnitId);

    when(searchSelectorService.buildPrePopulatedSelections(any(), any())).thenCallRealMethod();
    when(portalOrganisationAccessor.getOrganisationUnitOrError(organisationUnit.getOuId())).thenReturn(organisationUnit);

    final var result = projectTransferModelService.getPreSelectedPublishableOrganisation(form);

    assertThat(result).containsExactly(
        entry(organisationUnitId, organisationUnit.getName())
    );
  }
}