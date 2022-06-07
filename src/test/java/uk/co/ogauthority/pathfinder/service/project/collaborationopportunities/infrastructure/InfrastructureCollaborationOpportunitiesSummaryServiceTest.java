package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.infrastructure.InfrastructureCollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.service.project.AccessService;
import uk.co.ogauthority.pathfinder.testutil.InfrastructureCollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

@RunWith(MockitoJUnitRunner.class)
public class InfrastructureCollaborationOpportunitiesSummaryServiceTest {
  @Mock
  private InfrastructureCollaborationOpportunitiesService infrastructureCollaborationOpportunitiesService;

  @Mock
  private InfrastructureCollaborationOpportunityFileLinkService infrastructureCollaborationOpportunityFileLinkService;

  @Mock
  private AccessService accessService;

  private InfrastructureCollaborationOpportunitiesSummaryService infrastructureCollaborationOpportunitiesSummaryService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private final InfrastructureCollaborationOpportunity opportunity = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(detail);

  private final InfrastructureCollaborationOpportunity manualEntryOpportunity = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity_manualEntry(detail);

  @Before
  public void setUp() {
    infrastructureCollaborationOpportunitiesSummaryService = new InfrastructureCollaborationOpportunitiesSummaryService(
        infrastructureCollaborationOpportunitiesService,
        infrastructureCollaborationOpportunityFileLinkService,
        accessService);
    when(infrastructureCollaborationOpportunitiesService.getOpportunitiesForDetail(detail)).thenReturn(
        List.of(opportunity, manualEntryOpportunity)
    );
  }


  @Test
  public void getValidatedSummaryViews_allValid() {
    when(infrastructureCollaborationOpportunitiesService.isValid(any(), any())).thenReturn(true);
    var views = infrastructureCollaborationOpportunitiesSummaryService.getValidatedSummaryViews(detail);
    assertThat(views.size()).isEqualTo(2);
    var view1 = views.get(0);
    var view2 = views.get(1);
    assertThat(view1.isValid()).isTrue();
    assertThat(view2.isValid()).isTrue();
  }

  @Test
  public void getValidatedSummaryViews_containsInvalidEntry() {
    when(infrastructureCollaborationOpportunitiesService.isValid(opportunity, ValidationType.FULL)).thenReturn(true);
    var views = infrastructureCollaborationOpportunitiesSummaryService.getValidatedSummaryViews(detail);
    assertThat(views.size()).isEqualTo(2);
    var view1 = views.get(0);
    var view2 = views.get(1);
    assertThat(view1.isValid()).isTrue();
    assertThat(view2.isValid()).isFalse();
  }

  @Test
  public void getErrors() {
    var views = List.of(
        InfrastructureCollaborationOpportunityTestUtil.getView(UpcomingTenderUtil.DISPLAY_ORDER, true),
        InfrastructureCollaborationOpportunityTestUtil.getView(2, false),
        InfrastructureCollaborationOpportunityTestUtil.getView(3, false)
    );
    var errors = infrastructureCollaborationOpportunitiesSummaryService.getErrors(views);
    assertThat(errors.size()).isEqualTo(2);
    assertThat(errors.get(0).getDisplayOrder()).isEqualTo(2);
    assertThat(errors.get(0).getFieldName()).isEqualTo(String.format(
        InfrastructureCollaborationOpportunitiesSummaryService.ERROR_FIELD_NAME, 2));
    assertThat(errors.get(0).getErrorMessage()).isEqualTo(String.format(
        InfrastructureCollaborationOpportunitiesSummaryService.ERROR_MESSAGE, 2));
    assertThat(errors.get(1).getDisplayOrder()).isEqualTo(3);
    assertThat(errors.get(1).getFieldName()).isEqualTo(String.format(
        InfrastructureCollaborationOpportunitiesSummaryService.ERROR_FIELD_NAME, 3));
    assertThat(errors.get(1).getErrorMessage()).isEqualTo(String.format(
        InfrastructureCollaborationOpportunitiesSummaryService.ERROR_MESSAGE, 3));
  }

  @Test
  public void getErrors_emptyList() {
    var errors = infrastructureCollaborationOpportunitiesSummaryService.getErrors(Collections.emptyList());
    assertThat(errors.size()).isEqualTo(1);
    assertThat(errors.get(0).getDisplayOrder()).isEqualTo(1);
    assertThat(errors.get(0).getFieldName()).isEqualTo(InfrastructureCollaborationOpportunitiesSummaryService.EMPTY_LIST_ERROR);
    assertThat(errors.get(0).getErrorMessage()).isEqualTo(InfrastructureCollaborationOpportunitiesSummaryService.EMPTY_LIST_ERROR);
  }

  private void checkCommonFields(InfrastructureCollaborationOpportunityView view, InfrastructureCollaborationOpportunity opportunity) {
    assertThat(view.getDescriptionOfWork()).isEqualTo(opportunity.getDescriptionOfWork());
    assertThat(view.getUrgentResponseNeeded()).isEqualTo(StringDisplayUtil.yesNoFromBoolean(opportunity.getUrgentResponseNeeded()));
    assertThat(view.getContactName()).isEqualTo(opportunity.getContactName());
    assertThat(view.getContactPhoneNumber()).isEqualTo(opportunity.getPhoneNumber());
    assertThat(view.getContactJobTitle()).isEqualTo(opportunity.getJobTitle());
    assertThat(view.getContactEmailAddress()).isEqualTo(opportunity.getEmailAddress());
    assertThat(view.getSummaryLinks()).extracting(SummaryLink::getLinkText).containsExactly(
        SummaryLinkText.EDIT.getDisplayName(),
        SummaryLinkText.DELETE.getDisplayName()
    );
  }

  @Test
  public void canShowInTaskList_whenCanShowInTaskList_thenTrue() {
    when(infrastructureCollaborationOpportunitiesService.isTaskValidForProjectDetail(detail)).thenReturn(true);

    assertThat(infrastructureCollaborationOpportunitiesSummaryService.canShowInTaskList(detail)).isTrue();
  }

  @Test
  public void canShowInTaskList_whenCannotShowInTaskList_thenFalse() {
    when(infrastructureCollaborationOpportunitiesService.isTaskValidForProjectDetail(detail)).thenReturn(false);

    assertThat(infrastructureCollaborationOpportunitiesSummaryService.canShowInTaskList(detail)).isFalse();
  }

  @Test
  public void getSummaryViews_withProjectAndVersion_whenFound_thenReturnPopulatedList() {

    final var collaborationOpportunity = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(detail);

    final var project = detail.getProject();
    final var version = detail.getVersion();

    when(accessService.canCurrentUserAccessProjectSectionInfo(eq(detail), any())).thenReturn(true);
    when(infrastructureCollaborationOpportunitiesService.getOpportunitiesForProjectVersion(project, version))
        .thenReturn(List.of(collaborationOpportunity));

    final var result = infrastructureCollaborationOpportunitiesSummaryService.getSummaryViews(project, version);

    assertThat(result).hasSize(1);

    final var collaborationOpportunityView = result.get(0);

    assertThat(collaborationOpportunityView.getFunction().getValue()).isEqualTo(collaborationOpportunity.getFunction().getDisplayName());
    assertThat(collaborationOpportunityView.getFunction().getTag()).isEqualTo(Tag.NONE);
    checkCommonFields(collaborationOpportunityView, collaborationOpportunity);

  }

  @Test
  public void getSummaryViews_withProjectAndVersion_whenNotFound_thenReturnEmptyList() {

    final var project = detail.getProject();
    final var version = detail.getVersion();

    when(infrastructureCollaborationOpportunitiesService.getOpportunitiesForProjectVersion(project, version))
        .thenReturn(Collections.emptyList());

    final var result = infrastructureCollaborationOpportunitiesSummaryService.getSummaryViews(project, version);
    assertThat(result).isEmpty();
  }
}
