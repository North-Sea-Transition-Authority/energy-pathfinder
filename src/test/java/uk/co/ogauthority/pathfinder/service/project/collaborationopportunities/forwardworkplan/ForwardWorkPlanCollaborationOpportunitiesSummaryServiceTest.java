package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan.ForwardWorkPlanCollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan.ForwardWorkPlanCollaborationOpportunityViewUtil;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanCollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanCollaborationOpportunitiesSummaryServiceTest {

  @Mock
  private ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService;

  @Mock
  private ForwardWorkPlanCollaborationOpportunityFileLinkService forwardWorkPlanCollaborationOpportunityFileLinkService;

  @Mock
  private ProjectSectionItemOwnershipService projectSectionItemOwnershipService;

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  private ForwardWorkPlanCollaborationOpportunitiesSummaryService forwardWorkPlanCollaborationOpportunitiesSummaryService;

  private final PortalOrganisationGroup addedByPortalOrganisationGroup =
      TeamTestingUtil.generateOrganisationGroup(1, "org", "org");

  @Before
  public void setup() {
    forwardWorkPlanCollaborationOpportunitiesSummaryService = new ForwardWorkPlanCollaborationOpportunitiesSummaryService(
        forwardWorkPlanCollaborationOpportunityService,
        forwardWorkPlanCollaborationOpportunityFileLinkService,
        projectSectionItemOwnershipService,
        portalOrganisationAccessor
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

    when(portalOrganisationAccessor.getOrganisationGroupById(collaborationOpportunity.getAddedByOrganisationGroup()))
        .thenReturn(Optional.of(addedByPortalOrganisationGroup));
    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(eq(projectDetail), any())).thenReturn(true);

    ForwardWorkPlanCollaborationOpportunityView expectedCollaborationOpportunityView = new ForwardWorkPlanCollaborationOpportunityViewUtil.ForwardWorkPlanCollaborationOpportunityViewBuilder(
        collaborationOpportunity,
        1,
        Collections.emptyList(),
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(true)
        .build();

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

    ForwardWorkPlanCollaborationOpportunityView expectedCollaborationOpportunityView = new ForwardWorkPlanCollaborationOpportunityViewUtil.ForwardWorkPlanCollaborationOpportunityViewBuilder(
        collaborationOpportunity,
        1,
        Collections.emptyList(),
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(true)
        .build();

    when(portalOrganisationAccessor.getOrganisationGroupById(collaborationOpportunity.getAddedByOrganisationGroup()))
        .thenReturn(Optional.of(addedByPortalOrganisationGroup));
    when(forwardWorkPlanCollaborationOpportunityService.getOpportunitiesForProjectVersion(project, version)).thenReturn(
        List.of(collaborationOpportunity)
    );
    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(eq(projectDetail), any())).thenReturn(true);

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

    when(portalOrganisationAccessor.getOrganisationGroupById(collaborationOpportunity.getAddedByOrganisationGroup()))
        .thenReturn(Optional.of(addedByPortalOrganisationGroup));
    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(eq(projectDetail), any())).thenReturn(true);

    ForwardWorkPlanCollaborationOpportunityView expectedCollaborationOpportunityView = new ForwardWorkPlanCollaborationOpportunityViewUtil.ForwardWorkPlanCollaborationOpportunityViewBuilder(
        collaborationOpportunity,
        1,
        Collections.emptyList(),
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(true)
        .isValid(isValid)
        .build();

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

    ForwardWorkPlanCollaborationOpportunityView validCollaborationOpportunityView = new ForwardWorkPlanCollaborationOpportunityViewUtil.ForwardWorkPlanCollaborationOpportunityViewBuilder(
        collaborationOpportunity,
        1,
        Collections.emptyList(),
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(true)
        .isValid(true)
        .build();

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

    ForwardWorkPlanCollaborationOpportunityView validCollaborationOpportunityView = new ForwardWorkPlanCollaborationOpportunityViewUtil.ForwardWorkPlanCollaborationOpportunityViewBuilder(
        collaborationOpportunity,
        1,
        Collections.emptyList(),
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(true)
        .isValid(true)
        .build();

    ForwardWorkPlanCollaborationOpportunityView invalidCollaborationOpportunityView = new ForwardWorkPlanCollaborationOpportunityViewUtil.ForwardWorkPlanCollaborationOpportunityViewBuilder(
        collaborationOpportunity,
        1,
        Collections.emptyList(),
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(true)
        .isValid(false)
        .build();

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

      when(forwardWorkPlanCollaborationOpportunityService.isTaskValidForProjectDetail(projectDetail)).thenCallRealMethod();

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
    var accessValid = true;

    ForwardWorkPlanCollaborationOpportunityView expectedView = new ForwardWorkPlanCollaborationOpportunityViewUtil.ForwardWorkPlanCollaborationOpportunityViewBuilder(
        collaborationOpportunity,
        displayOrder,
        Collections.emptyList(),
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(accessValid)
        .build();

    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(eq(projectDetail), any())).thenReturn(accessValid);
    when(portalOrganisationAccessor.getOrganisationGroupById(collaborationOpportunity.getAddedByOrganisationGroup()))
        .thenReturn(Optional.of(addedByPortalOrganisationGroup));

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
    var accessValid = true;

    ForwardWorkPlanCollaborationOpportunityView expectedView = new ForwardWorkPlanCollaborationOpportunityViewUtil.ForwardWorkPlanCollaborationOpportunityViewBuilder(
        collaborationOpportunity,
        displayOrder,
        Collections.emptyList(),
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(accessValid)
        .isValid(isValid)
        .build();

    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(eq(projectDetail), any())).thenReturn(accessValid);
    when(portalOrganisationAccessor.getOrganisationGroupById(collaborationOpportunity.getAddedByOrganisationGroup()))
        .thenReturn(Optional.of(addedByPortalOrganisationGroup));

    final var resultingView = forwardWorkPlanCollaborationOpportunitiesSummaryService.getView(
        collaborationOpportunity,
        displayOrder,
        isValid
    );

    assertThat(resultingView).isEqualTo(expectedView);
  }

}