package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.CollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.testutil.CollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class CollaborationOpportunitiesSummaryServiceTest {
  @Mock
  private CollaborationOpportunitiesService collaborationOpportunitiesService;

  @Mock
  private CollaborationOpportunityFileLinkService collaborationOpportunityFileLinkService;

  private CollaborationOpportunitiesSummaryService collaborationOpportunitiesSummaryService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private final CollaborationOpportunity opportunity = CollaborationOpportunityTestUtil.getCollaborationOpportunity(detail);

  private final CollaborationOpportunity manualEntryOpportunity = CollaborationOpportunityTestUtil.getCollaborationOpportunity_manualEntry(detail);

  @Before
  public void setUp() {
    collaborationOpportunitiesSummaryService = new CollaborationOpportunitiesSummaryService(
        collaborationOpportunitiesService,
        collaborationOpportunityFileLinkService
    );
    when(collaborationOpportunitiesService.getOpportunitiesForDetail(detail)).thenReturn(
        List.of(opportunity, manualEntryOpportunity)
    );
  }


  @Test
  public void getValidatedSummaryViews_allValid() {
    when(collaborationOpportunitiesService.isValid(any(), any())).thenReturn(true);
    var views = collaborationOpportunitiesSummaryService.getValidatedSummaryViews(detail);
    assertThat(views.size()).isEqualTo(2);
    var view1 = views.get(0);
    var view2 = views.get(1);
    assertThat(view1.isValid()).isTrue();
    assertThat(view2.isValid()).isTrue();
  }

  @Test
  public void getValidatedSummaryViews_containsInvalidEntry() {
    when(collaborationOpportunitiesService.isValid(opportunity, ValidationType.FULL)).thenReturn(true);
    var views = collaborationOpportunitiesSummaryService.getValidatedSummaryViews(detail);
    assertThat(views.size()).isEqualTo(2);
    var view1 = views.get(0);
    var view2 = views.get(1);
    assertThat(view1.isValid()).isTrue();
    assertThat(view2.isValid()).isFalse();
  }

  @Test
  public void getErrors() {
    var views = List.of(
        CollaborationOpportunityTestUtil.getView(UpcomingTenderUtil.DISPLAY_ORDER, true),
        CollaborationOpportunityTestUtil.getView(2, false),
        CollaborationOpportunityTestUtil.getView(3, false)
    );
    var errors = collaborationOpportunitiesSummaryService.getErrors(views);
    assertThat(errors.size()).isEqualTo(2);
    assertThat(errors.get(0).getDisplayOrder()).isEqualTo(2);
    assertThat(errors.get(0).getFieldName()).isEqualTo(String.format(CollaborationOpportunitiesSummaryService.ERROR_FIELD_NAME, 2));
    assertThat(errors.get(0).getErrorMessage()).isEqualTo(String.format(CollaborationOpportunitiesSummaryService.ERROR_MESSAGE, 2));
    assertThat(errors.get(1).getDisplayOrder()).isEqualTo(3);
    assertThat(errors.get(1).getFieldName()).isEqualTo(String.format(CollaborationOpportunitiesSummaryService.ERROR_FIELD_NAME, 3));
    assertThat(errors.get(1).getErrorMessage()).isEqualTo(String.format(CollaborationOpportunitiesSummaryService.ERROR_MESSAGE, 3));
  }

  @Test
  public void getErrors_emptyList() {
    var errors = collaborationOpportunitiesSummaryService.getErrors(Collections.emptyList());
    assertThat(errors.size()).isEqualTo(1);
    assertThat(errors.get(0).getDisplayOrder()).isEqualTo(1);
    assertThat(errors.get(0).getFieldName()).isEqualTo(CollaborationOpportunitiesSummaryService.EMPTY_LIST_ERROR);
    assertThat(errors.get(0).getErrorMessage()).isEqualTo(CollaborationOpportunitiesSummaryService.EMPTY_LIST_ERROR);
  }

  private void checkCommonFields(CollaborationOpportunityView view, CollaborationOpportunity opportunity) {
    assertThat(view.getDescriptionOfWork()).isEqualTo(opportunity.getDescriptionOfWork());
    assertThat(view.getEstimatedServiceDate()).isEqualTo(DateUtil.formatDate(opportunity.getEstimatedServiceDate()));
    assertThat(view.getContactDetailView().getName()).isEqualTo(opportunity.getContactName());
    assertThat(view.getContactDetailView().getPhoneNumber()).isEqualTo(opportunity.getPhoneNumber());
    assertThat(view.getContactDetailView().getJobTitle()).isEqualTo(opportunity.getJobTitle());
    assertThat(view.getContactDetailView().getEmailAddress()).isEqualTo(opportunity.getEmailAddress());
    assertThat(view.getEditLink().getLinkText()).isEqualTo(SummaryLinkText.EDIT.getDisplayName());
    assertThat(view.getDeleteLink().getLinkText()).isEqualTo(SummaryLinkText.DELETE.getDisplayName());
  }
}
