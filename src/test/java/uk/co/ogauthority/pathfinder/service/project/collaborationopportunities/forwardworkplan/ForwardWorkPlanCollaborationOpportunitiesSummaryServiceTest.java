package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan.ForwardWorkPlanCollaborationOpportunityViewUtil;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanCollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanCollaborationOpportunitiesSummaryServiceTest {

  @Mock
  private ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService;

  @Mock
  private ForwardWorkPlanCollaborationOpportunityFileLinkService forwardWorkPlanCollaborationOpportunityFileLinkService;

  private ForwardWorkPlanCollaborationOpportunitiesSummaryService forwardWorkPlanCollaborationOpportunitiesSummaryService;

  @Before
  public void setup() {
    forwardWorkPlanCollaborationOpportunitiesSummaryService = new ForwardWorkPlanCollaborationOpportunitiesSummaryService(
        forwardWorkPlanCollaborationOpportunityService,
        forwardWorkPlanCollaborationOpportunityFileLinkService
    );
  }

  @Test
  public void getSummaryViews_whenProjectDetailVariantAndNoCollaborationsFound_thenEmptyList() {

    final var projectDetail = ProjectUtil.getProjectDetails();

    when(forwardWorkPlanCollaborationOpportunityService.getOpportunitiesForDetail(projectDetail)).thenReturn(
        Collections.emptyList()
    );

    final var summaryViews = forwardWorkPlanCollaborationOpportunitiesSummaryService.getSummaryViews(
        projectDetail
    );

    assertThat(summaryViews).isEmpty();
  }

  @Test
  public void getSummaryViews_whenProjectDetailVariantAndCollaborationsFound_thenPopulatedList() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var collaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(
        projectDetail
    );

    final var expectedCollaborationOpportunityView = ForwardWorkPlanCollaborationOpportunityViewUtil.createView(
        collaborationOpportunity,
        1,
        List.of()
    );

    when(forwardWorkPlanCollaborationOpportunityService.getOpportunitiesForDetail(projectDetail)).thenReturn(
        List.of(collaborationOpportunity)
    );

    final var summaryViews = forwardWorkPlanCollaborationOpportunitiesSummaryService.getSummaryViews(
        projectDetail
    );

    assertThat(summaryViews).containsExactly(expectedCollaborationOpportunityView);
  }

  @Test
  public void getSummaryViews_whenProjectAndVersionVariantVariantAndNoCollaborationsFound_thenEmptyList() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var project = projectDetail.getProject();
    final var version = projectDetail.getVersion();

    when(forwardWorkPlanCollaborationOpportunityService.getOpportunitiesForProjectVersion(project, version)).thenReturn(
        Collections.emptyList()
    );

    final var summaryViews = forwardWorkPlanCollaborationOpportunitiesSummaryService.getSummaryViews(
        projectDetail.getProject(),
        projectDetail.getVersion()
    );

    assertThat(summaryViews).isEmpty();
  }

  @Test
  public void getSummaryViews_whenProjectAndVersionVariantAndCollaborationsFound_thenPopulatedList() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var project = projectDetail.getProject();
    final var version = projectDetail.getVersion();

    final var collaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(
        projectDetail
    );

    final var expectedCollaborationOpportunityView = ForwardWorkPlanCollaborationOpportunityViewUtil.createView(
        collaborationOpportunity,
        1,
        List.of()
    );

    when(forwardWorkPlanCollaborationOpportunityService.getOpportunitiesForProjectVersion(project, version)).thenReturn(
        List.of(collaborationOpportunity)
    );

    final var summaryViews = forwardWorkPlanCollaborationOpportunitiesSummaryService.getSummaryViews(
        project,
        version
    );

    assertThat(summaryViews).containsExactly(expectedCollaborationOpportunityView);
  }

  @Test
  public void getValidatedSummaryViews_whenInvalid_validPropertyIsFalse() {
    assertValidatedSummaryView(false);
  }

  @Test
  public void getValidatedSummaryViews_whenValid_validPropertyIsTrue() {
    assertValidatedSummaryView(true);
  }

  private void assertValidatedSummaryView(boolean isValid) {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var collaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(
        projectDetail
    );

    final var expectedCollaborationOpportunityView = ForwardWorkPlanCollaborationOpportunityViewUtil.createView(
        collaborationOpportunity,
        1,
        List.of(),
        isValid
    );

    when(forwardWorkPlanCollaborationOpportunityService.getOpportunitiesForDetail(projectDetail)).thenReturn(
        List.of(collaborationOpportunity)
    );

    when(forwardWorkPlanCollaborationOpportunityService.isValid(
        collaborationOpportunity,
        ValidationType.FULL
    )).thenReturn(isValid);

    final var summaryViews = forwardWorkPlanCollaborationOpportunitiesSummaryService.getValidatedSummaryViews(
        projectDetail
    );

    assertThat(summaryViews).containsExactly(expectedCollaborationOpportunityView);
  }

  @Test
  public void getErrors_whenNoErrors_thenEmptyList() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var collaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(
        projectDetail
    );
    final var isValid = true;

    final var collaborationOpportunityView = ForwardWorkPlanCollaborationOpportunityViewUtil.createView(
        collaborationOpportunity,
        1,
        List.of(),
        isValid
    );

    final var errorList = forwardWorkPlanCollaborationOpportunitiesSummaryService.getErrors(List.of(collaborationOpportunityView));
    assertThat(errorList).isEmpty();
  }

  @Test
  public void getErrors_whenErrors_thenAssertExpectedMessages() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var collaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(
        projectDetail
    );

    final var validCollaborationOpportunityView = ForwardWorkPlanCollaborationOpportunityViewUtil.createView(
        collaborationOpportunity,
        1,
        List.of(),
        true
    );

    final var invalidCollaborationOpportunityView = ForwardWorkPlanCollaborationOpportunityViewUtil.createView(
        collaborationOpportunity,
        2,
        List.of(),
        false
    );

    final var errorList = forwardWorkPlanCollaborationOpportunitiesSummaryService.getErrors(
        List.of(validCollaborationOpportunityView, invalidCollaborationOpportunityView)
    );

    assertThat(errorList).hasSize(1);

    final var invalidViewDisplayOrder = invalidCollaborationOpportunityView.getDisplayOrder();
    assertThat(errorList.get(0).getDisplayOrder()).isEqualTo(invalidViewDisplayOrder);
    assertThat(errorList.get(0).getFieldName()).isEqualTo(String.format(
        ForwardWorkPlanCollaborationOpportunitiesSummaryService.ERROR_FIELD_NAME,
        invalidViewDisplayOrder
    ));
    assertThat(errorList.get(0).getErrorMessage()).isEqualTo(String.format(
        ForwardWorkPlanCollaborationOpportunitiesSummaryService.ERROR_MESSAGE,
        invalidViewDisplayOrder
    ));

  }

  @Test
  public void validateViews_whenNoViews_thenInvalid() {
    final var validationResult = forwardWorkPlanCollaborationOpportunitiesSummaryService.validateViews(List.of());
    assertThat(validationResult).isEqualTo(ValidationResult.INVALID);
  }

  @Test
  public void validateViews_whenAllViewsValid_thenValid() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var collaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(
        projectDetail
    );

    final var validCollaborationOpportunityView = ForwardWorkPlanCollaborationOpportunityViewUtil.createView(
        collaborationOpportunity,
        1,
        List.of(),
        true
    );

    final var validationResult = forwardWorkPlanCollaborationOpportunitiesSummaryService.validateViews(
        List.of(validCollaborationOpportunityView)
    );

    assertThat(validationResult).isEqualTo(ValidationResult.VALID);
  }

  @Test
  public void validateViews_whenNotAllViewsValid_thenInvalid() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var collaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(
        projectDetail
    );

    final var validCollaborationOpportunityView = ForwardWorkPlanCollaborationOpportunityViewUtil.createView(
        collaborationOpportunity,
        1,
        List.of(),
        true
    );

    final var invalidCollaborationOpportunityView = ForwardWorkPlanCollaborationOpportunityViewUtil.createView(
        collaborationOpportunity,
        1,
        List.of(),
        false
    );

    final var validationResult = forwardWorkPlanCollaborationOpportunitiesSummaryService.validateViews(
        List.of(validCollaborationOpportunityView, invalidCollaborationOpportunityView)
    );

    assertThat(validationResult).isEqualTo(ValidationResult.INVALID);
  }

  @Test
  public void canShowInTaskList_smokeTestProjectTypes() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var permittedProjectTypes = Set.of(ProjectType.FORWARD_WORK_PLAN);

    Arrays.asList(ProjectType.values()).forEach(projectType -> {

      projectDetail.setProjectType(projectType);

      when(forwardWorkPlanCollaborationOpportunityService.canShowInTaskList(projectDetail)).thenCallRealMethod();

      final var canShowInTaskList = forwardWorkPlanCollaborationOpportunitiesSummaryService.canShowInTaskList(projectDetail);

      if (permittedProjectTypes.contains(projectType)) {
        assertThat(canShowInTaskList).isTrue();
      } else {
        assertThat(canShowInTaskList).isFalse();
      }
    });
  }

  @Test
  public void isValid_whenValid_thenTrue() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var collaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(
        projectDetail
    );
    final var validationType = ValidationType.FULL;

    when(forwardWorkPlanCollaborationOpportunityService.isValid(collaborationOpportunity, validationType)).thenReturn(true);

    final var isValid = forwardWorkPlanCollaborationOpportunitiesSummaryService.isValid(collaborationOpportunity, validationType);
    assertThat(isValid).isTrue();
  }

  @Test
  public void isValid_whenInvalid_thenFalse() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var collaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(
        projectDetail
    );
    final var validationType = ValidationType.FULL;

    when(forwardWorkPlanCollaborationOpportunityService.isValid(collaborationOpportunity, validationType)).thenReturn(false);

    final var isValid = forwardWorkPlanCollaborationOpportunitiesSummaryService.isValid(collaborationOpportunity, validationType);
    assertThat(isValid).isFalse();
  }

  @Test
  public void getView_withoutValidVariant_assertExpectedReturnValue() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var collaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(
        projectDetail
    );
    final var displayOrder = 10;

    final var expectedView = ForwardWorkPlanCollaborationOpportunityViewUtil.createView(
        collaborationOpportunity,
        displayOrder,
        List.of()
    );

    final var resultingView = forwardWorkPlanCollaborationOpportunitiesSummaryService.getView(
        collaborationOpportunity,
        displayOrder
    );

    assertThat(resultingView).isEqualTo(expectedView);
  }

  @Test
  public void getView_withValidVariant_assertExpectedReturnValue() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var collaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(
        projectDetail
    );
    final var displayOrder = 10;
    final var isValid = true;

    final var expectedView = ForwardWorkPlanCollaborationOpportunityViewUtil.createView(
        collaborationOpportunity,
        displayOrder,
        List.of(),
        isValid
    );

    final var resultingView = forwardWorkPlanCollaborationOpportunitiesSummaryService.getView(
        collaborationOpportunity,
        displayOrder,
        isValid
    );

    assertThat(resultingView).isEqualTo(expectedView);
  }

}