package uk.co.ogauthority.pathfinder.service.project.upcomingtender;

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
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.upcomingtender.UpcomingTenderView;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.UploadedFileUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class UpcomingTenderSummaryServiceTest {

  @Mock
  private UpcomingTenderService upcomingTenderService;

  @Mock
  private UpcomingTenderFileLinkService upcomingTenderFileLinkService;

  private UpcomingTenderSummaryService upcomingTenderSummaryService;

  private final ProjectDetail details = ProjectUtil.getProjectDetails();

  private final UpcomingTender upcomingTender = UpcomingTenderUtil.getUpcomingTender(details);

  private final UpcomingTender manualEntryUpcomingTender = UpcomingTenderUtil.getUpcomingTender_manualEntry(details);

  @Before
  public void setUp() {
    upcomingTenderSummaryService = new UpcomingTenderSummaryService(upcomingTenderService, upcomingTenderFileLinkService);
    when(upcomingTenderService.getUpcomingTendersForDetail(details)).thenReturn(
        List.of(upcomingTender, manualEntryUpcomingTender)
    );
  }

  @Test
  public void getSummaryViews() {
    var views = upcomingTenderSummaryService.getSummaryViews(details);
    assertThat(views.size()).isEqualTo(2);
    var view1 = views.get(0);
    var view2 = views.get(1);
    assertThat(view1.getTenderFunction()).isEqualTo(upcomingTender.getTenderFunction().getDisplayName());
    assertThat(view1.getDisplayOrder()).isEqualTo(1);
    assertThat(view2.getTenderFunction()).isEqualTo(manualEntryUpcomingTender.getManualTenderFunction());
    assertThat(view2.getDisplayOrder()).isEqualTo(2);
    checkCommonFields(view1, upcomingTender);
    checkCommonFields(view2, manualEntryUpcomingTender);
  }

  @Test
  public void getValidatedSummaryViews_allValid() {
    when(upcomingTenderService.isValid(any(), any())).thenReturn(true);
    var views = upcomingTenderSummaryService.getValidatedSummaryViews(details);
    assertThat(views.size()).isEqualTo(2);
    var view1 = views.get(0);
    var view2 = views.get(1);
    assertThat(view1.isValid()).isTrue();
    assertThat(view2.isValid()).isTrue();
  }

  @Test
  public void getValidatedSummaryViews_containsInvalidEntry() {
    when(upcomingTenderService.isValid(upcomingTender, ValidationType.FULL)).thenReturn(true);
    var views = upcomingTenderSummaryService.getValidatedSummaryViews(details);
    assertThat(views.size()).isEqualTo(2);
    var view1 = views.get(0);
    var view2 = views.get(1);
    assertThat(view1.isValid()).isTrue();
    assertThat(view2.isValid()).isFalse();
  }

  @Test
  public void getErrors() {
    var views = List.of(
        UpcomingTenderUtil.getView(UpcomingTenderUtil.DISPLAY_ORDER, true),
        UpcomingTenderUtil.getView(2, false),
        UpcomingTenderUtil.getView(3, false)
    );
    var errors = upcomingTenderSummaryService.getErrors(views);
    assertThat(errors.size()).isEqualTo(2);
    assertThat(errors.get(0).getDisplayOrder()).isEqualTo(2);
    assertThat(errors.get(0).getFieldName()).isEqualTo(String.format(UpcomingTenderSummaryService.ERROR_FIELD_NAME, 2));
    assertThat(errors.get(0).getErrorMessage()).isEqualTo(String.format(UpcomingTenderSummaryService.ERROR_MESSAGE, 2));
    assertThat(errors.get(1).getDisplayOrder()).isEqualTo(3);
    assertThat(errors.get(1).getFieldName()).isEqualTo(String.format(UpcomingTenderSummaryService.ERROR_FIELD_NAME, 3));
    assertThat(errors.get(1).getErrorMessage()).isEqualTo(String.format(UpcomingTenderSummaryService.ERROR_MESSAGE, 3));
  }

  @Test
  public void getErrors_emptyList() {
    var errors = upcomingTenderSummaryService.getErrors(Collections.emptyList());
    assertThat(errors.size()).isEqualTo(1);
    assertThat(errors.get(0).getDisplayOrder()).isEqualTo(1);
    assertThat(errors.get(0).getFieldName()).isEqualTo(UpcomingTenderSummaryService.EMPTY_LIST_ERROR);
    assertThat(errors.get(0).getErrorMessage()).isEqualTo(UpcomingTenderSummaryService.EMPTY_LIST_ERROR);
  }

  @Test
  public void getSummaryViews_withFileUpload() {

    var uploadedFileView = UploadedFileUtil.createUploadedFileView();
    when(upcomingTenderFileLinkService.getFileUploadViewsLinkedToUpcomingTender(any())).thenReturn(List.of(uploadedFileView));

    when(upcomingTenderService.getUpcomingTendersForDetail(details)).thenReturn(List.of(upcomingTender));

    var views = upcomingTenderSummaryService.getSummaryViews(details);
    assertThat(views.size()).isEqualTo(1);

    var view = views.get(0);
    checkCommonFields(view, upcomingTender);
    assertThat(view.getUploadedFileViews()).hasSize(1);

    var resultingFile = view.getUploadedFileViews().get(0);
    assertThat(resultingFile.getFileId()).isEqualTo(uploadedFileView.getFileId());
    assertThat(resultingFile.getFileDescription()).isEqualTo(uploadedFileView.getFileDescription());
    assertThat(resultingFile.getFileName()).isEqualTo(uploadedFileView.getFileName());
    assertThat(resultingFile.getFileSize()).isEqualTo(uploadedFileView.getFileSize());
    assertThat(resultingFile.getFileUploadedTime()).isEqualTo(uploadedFileView.getFileUploadedTime());
    assertThat(resultingFile.getFileUrl()).isEqualTo(uploadedFileView.getFileUrl());
  }

  @Test
  public void getSummaryViews_withoutFileUpload() {

    when(upcomingTenderFileLinkService.getFileUploadViewsLinkedToUpcomingTender(any())).thenReturn(List.of());

    when(upcomingTenderService.getUpcomingTendersForDetail(details)).thenReturn(List.of(upcomingTender));

    var views = upcomingTenderSummaryService.getSummaryViews(details);
    assertThat(views.size()).isEqualTo(1);

    var view = views.get(0);
    checkCommonFields(view, upcomingTender);
    assertThat(view.getUploadedFileViews()).isEmpty();
  }

  private void checkCommonFields(UpcomingTenderView view, UpcomingTender tender) {
    assertThat(view.getDescriptionOfWork()).isEqualTo(tender.getDescriptionOfWork());
    assertThat(view.getEstimatedTenderDate()).isEqualTo(DateUtil.formatDate(tender.getEstimatedTenderDate()));
    assertThat(view.getContractBand()).isEqualTo(tender.getContractBand().getDisplayName());

    var contactDetailView = view.getContactDetailView();
    assertThat(contactDetailView.getName()).isEqualTo(tender.getContactName());
    assertThat(contactDetailView.getPhoneNumber()).isEqualTo(tender.getPhoneNumber());
    assertThat(contactDetailView.getJobTitle()).isEqualTo(tender.getJobTitle());
    assertThat(contactDetailView.getEmailAddress()).isEqualTo(tender.getEmailAddress());

    assertThat(view.getSummaryLinks()).extracting(SummaryLink::getLinkText).containsExactly(
        SummaryLinkText.EDIT.getDisplayName(),
        SummaryLinkText.DELETE.getDisplayName()
    );
  }
}
