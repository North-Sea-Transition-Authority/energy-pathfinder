package uk.co.ogauthority.pathfinder.service.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.ProjectOperatorForm;
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.ProjectOperatorFormValidator;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.project.selectoperator.SelectOperatorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectFormSectionServiceTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class SelectOperatorServiceTest {

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  @Mock
  private ValidationService validationService;

  @Mock
  private ProjectOperatorService projectOperatorService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  @Mock
  private ProjectOperatorFormValidator projectOperatorFormValidator;

  private SelectOperatorService selectOperatorService;

  private static final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  private static final PortalOrganisationGroup organisationGroup = TeamTestingUtil.generateOrganisationGroup(
      1,
      "Org Grp",
      "Org Grp"
  );

  @Before
  public void setUp() {
    selectOperatorService = new SelectOperatorService(
        portalOrganisationAccessor,
        validationService,
        projectOperatorService,
        entityDuplicationService,
        projectOperatorFormValidator
    );
  }

  @Test
  public void getOrganisationGroupOrError_userCanAccessGroup() {
    when(portalOrganisationAccessor.getOrganisationGroupOrError(organisationGroup.getOrgGrpId())).thenReturn(organisationGroup);
    when(projectOperatorService.canUserAccessOrgGroup(authenticatedUser, organisationGroup)).thenReturn(true);
    assertThat(selectOperatorService.getOrganisationGroupOrError(authenticatedUser, organisationGroup.getOrgGrpId())).isEqualTo(organisationGroup);
  }

  @Test(expected = AccessDeniedException.class)
  public void getOrganisationGroupOrError_userCannotAccessGroup() {
    when(portalOrganisationAccessor.getOrganisationGroupOrError(organisationGroup.getOrgGrpId())).thenReturn(organisationGroup);
    when(projectOperatorService.canUserAccessOrgGroup(authenticatedUser, organisationGroup)).thenReturn(false);
    selectOperatorService.getOrganisationGroupOrError(authenticatedUser, organisationGroup.getOrgGrpId());
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getOrganisationGroupOrError_noMatchingGroup() {
    doThrow(new PathfinderEntityNotFoundException("test")).when(portalOrganisationAccessor).getOrganisationGroupOrError(organisationGroup.getOrgGrpId());
    selectOperatorService.getOrganisationGroupOrError(authenticatedUser, organisationGroup.getOrgGrpId());
  }

  @Test
  public void getForm_whenPublishedAsOperatorIsTrue_assertPopulatedFormProperties() {

    final var projectOperator = new ProjectOperator();
    projectOperator.setOrganisationGroup(organisationGroup);
    projectOperator.setIsPublishedAsOperator(true);

    when(projectOperatorService.getProjectOperatorByProjectDetail(detail)).thenReturn(Optional.of(projectOperator));

    final var resultingForm = selectOperatorService.getForm(detail);

    assertCommonOperatorFormProperties(resultingForm, projectOperator);
    assertThat(resultingForm.getPublishableOrganisation()).isNull();
  }

  @Test
  public void getForm_whenPublishedAsOperatorIsFalse_assertPopulatedFormProperties() {

    final var projectOperator = new ProjectOperator();
    projectOperator.setOrganisationGroup(organisationGroup);
    projectOperator.setIsPublishedAsOperator(false);

    final var publishableOrganisation = TeamTestingUtil.generateOrganisationUnit(100, "name", organisationGroup);
    projectOperator.setPublishableOrganisationUnit(publishableOrganisation);

    when(projectOperatorService.getProjectOperatorByProjectDetail(detail)).thenReturn(Optional.of(projectOperator));

    final var resultingForm = selectOperatorService.getForm(detail);

    assertCommonOperatorFormProperties(resultingForm, projectOperator);
    assertThat(resultingForm.getPublishableOrganisation()).isEqualTo(String.valueOf(projectOperator.getPublishableOrganisationUnit().getOuId()));
  }

  @Test
  public void getForm_whenPublishedAsOperatorIsNull_assertPopulatedFormProperties() {

    final var projectOperator = new ProjectOperator();
    projectOperator.setOrganisationGroup(organisationGroup);
    projectOperator.setIsPublishedAsOperator(null);

    when(projectOperatorService.getProjectOperatorByProjectDetail(detail)).thenReturn(Optional.of(projectOperator));

    final var resultingForm = selectOperatorService.getForm(detail);

    assertCommonOperatorFormProperties(resultingForm, projectOperator);
    assertThat(resultingForm.getPublishableOrganisation()).isNull();
  }

  @Test
  public void getForm_whenPublishedAsOperatorIsFalseAndPublishableOrganisationIsNull_assertPopulatedFormProperties() {

    final var projectOperator = new ProjectOperator();
    projectOperator.setOrganisationGroup(organisationGroup);
    projectOperator.setIsPublishedAsOperator(false);
    projectOperator.setPublishableOrganisationUnit(null);

    when(projectOperatorService.getProjectOperatorByProjectDetail(detail)).thenReturn(Optional.of(projectOperator));

    final var resultingForm = selectOperatorService.getForm(detail);

    assertCommonOperatorFormProperties(resultingForm, projectOperator);
    assertThat(resultingForm.getPublishableOrganisation()).isNull();
  }

  @Test
  public void validate() {
    var form = new ProjectOperatorForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    selectOperatorService.validate(
        form,
        bindingResult
    );
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  public void updateProjectOperator_assertProjectOperatorServiceInteraction() {

    final var projectOperatorForm = new ProjectOperatorForm();

    selectOperatorService.updateProjectOperator(detail, projectOperatorForm);

    verify(projectOperatorService, times(1)).createOrUpdateProjectOperator(detail, projectOperatorForm);
  }

  @Test
  public void copySectionData_verifyDuplicationServiceInteraction() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

    final var fromOperator = ProjectOperatorTestUtil.getOperator();

    when(projectOperatorService.getProjectOperatorByProjectDetail(fromProjectDetail))
        .thenReturn(Optional.of(fromOperator));

    selectOperatorService.copySectionData(fromProjectDetail, toProjectDetail);

    verify(entityDuplicationService, times(1)).duplicateEntityAndSetNewParent(
        fromOperator,
        toProjectDetail,
        ProjectOperator.class
    );
  }

  @Test
  public void canShowInTaskList_whenInfrastructureProject_thenTrue() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);
    assertThat(selectOperatorService.canShowInTaskList(projectDetail, Set.of(UserToProjectRelationship.OPERATOR)))
        .isTrue();
  }

  @Test
  public void canShowInTaskList_whenNotInfrastructureProject_thenFalse() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);
    assertThat(selectOperatorService.canShowInTaskList(projectDetail, Set.of(UserToProjectRelationship.OPERATOR)))
        .isFalse();
  }

  @Test
  public void canShowInTaskList_whenNullProjectType_thenFalse() {
    var projectDetail = ProjectUtil.getProjectDetails();
    projectDetail.setProjectType(null);
    assertThat(selectOperatorService.canShowInTaskList(projectDetail, Set.of(UserToProjectRelationship.OPERATOR)))
        .isFalse();
  }

  @Test
  public void canShowInTaskList_userToProjectRelationshipSmokeTest() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);
    ProjectFormSectionServiceTestUtil.canShowInTaskList_userToProjectRelationshipSmokeTest(
        selectOperatorService,
        projectDetail,
        Set.of(UserToProjectRelationship.OPERATOR)
    );
  }

  @Test
  public void isTaskValidForProjectDetail_whenInfrastructureProject_thenTrue() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);
    assertThat(selectOperatorService.isTaskValidForProjectDetail(projectDetail)).isTrue();
  }

  @Test
  public void isTaskValidForProjectDetail_whenNotInfrastructureProject_thenFalse() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);
    assertThat(selectOperatorService.isTaskValidForProjectDetail(projectDetail)).isFalse();
  }

  @Test
  public void getSupportedProjectTypes_verifyInfrastructureAndForwardWorkPlan() {
    assertThat(selectOperatorService.getSupportedProjectTypes()).containsExactlyInAnyOrder(
        ProjectType.INFRASTRUCTURE,
        ProjectType.FORWARD_WORK_PLAN
    );
  }

  @Test
  public void alwaysCopySectionData_smokeTestProjectTypes_assertOnlyForwardWorkPlanType() {

    final var projectDetail = ProjectUtil.getProjectDetails();

    final var projectTypesToAlwaysCopy = Set.of(ProjectType.FORWARD_WORK_PLAN);

    Arrays.asList(ProjectType.values()).forEach(projectType -> {

      projectDetail.setProjectType(projectType);

      final var alwaysCopySectionData = selectOperatorService.alwaysCopySectionData(projectDetail);

      if (projectTypesToAlwaysCopy.contains(projectType)) {
        assertThat(alwaysCopySectionData).isTrue();
      } else {
        assertThat(alwaysCopySectionData).isFalse();
      }

    });
  }

  @Test
  public void removeSectionData_verifyInteractions() {
    selectOperatorService.removeSectionData(detail);
    verify(projectOperatorService, times(1)).deleteProjectOperatorByProjectDetail(detail);
  }

  @Test
  public void allowSectionDataCleanUp_verifyIsFalse() {
    final var allowSectionDateCleanUp = selectOperatorService.allowSectionDataCleanUp(detail);
    assertThat(allowSectionDateCleanUp).isFalse();
  }

  private void assertCommonOperatorFormProperties(ProjectOperatorForm formToAssert, ProjectOperator sourceEntity) {
    assertThat(formToAssert.getOperator()).isEqualTo(String.valueOf(sourceEntity.getOrganisationGroup().getOrgGrpId()));
    assertThat(formToAssert.isPublishedAsOperator()).isEqualTo(sourceEntity.isPublishedAsOperator());
  }

}
