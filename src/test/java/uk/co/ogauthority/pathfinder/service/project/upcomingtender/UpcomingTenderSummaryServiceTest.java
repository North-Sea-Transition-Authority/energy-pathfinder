package uk.co.ogauthority.pathfinder.service.project.upcomingtender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.project.upcomingtender.UpcomingTenderConversionController;
import uk.co.ogauthority.pathfinder.controller.project.upcomingtender.UpcomingTendersController;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.model.view.upcomingtender.UpcomingTenderView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.UploadedFileUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class UpcomingTenderSummaryServiceTest {

  @Mock
  private UpcomingTenderService upcomingTenderService;

  @Mock
  private UpcomingTenderFileLinkService upcomingTenderFileLinkService;

  @Mock
  private ProjectSectionItemOwnershipService projectSectionItemOwnershipService;

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  private UpcomingTenderSummaryService upcomingTenderSummaryService;

  private final ProjectDetail details = ProjectUtil.getProjectDetails();

  private final UpcomingTender upcomingTender = UpcomingTenderUtil.getUpcomingTender(details);

  private final UpcomingTender manualEntryUpcomingTender = UpcomingTenderUtil.getUpcomingTender_manualEntry(details);

  private final PortalOrganisationGroup addedByPortalOrganisationGroup =
      TeamTestingUtil.generateOrganisationGroup(upcomingTender.getAddedByOrganisationGroup(), "org", "org");

  @Before
  public void setUp() {
    upcomingTenderSummaryService = new UpcomingTenderSummaryService(
        upcomingTenderService,
        upcomingTenderFileLinkService,
        projectSectionItemOwnershipService,
        portalOrganisationAccessor
    );
    when(upcomingTenderService.getUpcomingTendersForDetail(details)).thenReturn(
        List.of(upcomingTender, manualEntryUpcomingTender)
    );

    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(any(), any())).thenReturn(false);
    when(portalOrganisationAccessor.getOrganisationGroupById(upcomingTender.getAddedByOrganisationGroup()))
        .thenReturn(Optional.of(addedByPortalOrganisationGroup));
    when(portalOrganisationAccessor.getOrganisationGroupById(manualEntryUpcomingTender.getAddedByOrganisationGroup()))
        .thenReturn(Optional.of(addedByPortalOrganisationGroup));
  }

  @Test
  public void getSummaryViews() {
    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        eq(upcomingTender.getProjectDetail()),
        any())
    ).thenReturn(true);
    var views = upcomingTenderSummaryService.getSummaryViews(details);
    assertThat(views.size()).isEqualTo(2);
    var view1 = views.get(0);
    var view2 = views.get(1);
    var view1TenderFunction = view1.getTenderFunction();
    var view2TenderFunction = view2.getTenderFunction();
    assertThat(view1TenderFunction.getValue()).isEqualTo(upcomingTender.getTenderFunction().getDisplayName());
    assertThat(view1TenderFunction.getTag()).isEqualTo(Tag.NONE);
    assertThat(view1.getDisplayOrder()).isEqualTo(1);
    assertThat(view2TenderFunction.getValue()).isEqualTo(manualEntryUpcomingTender.getManualTenderFunction());
    assertThat(view2TenderFunction.getTag()).isEqualTo(Tag.NOT_FROM_LIST);
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

    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        eq(upcomingTender.getProjectDetail()),
        any())
    ).thenReturn(true);
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

    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        eq(upcomingTender.getProjectDetail()),
        any())
    ).thenReturn(true);
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

    assertThat(view.getContactName()).isEqualTo(tender.getContactName());
    assertThat(view.getContactPhoneNumber()).isEqualTo(tender.getPhoneNumber());
    assertThat(view.getContactJobTitle()).isEqualTo(tender.getJobTitle());
    assertThat(view.getContactEmailAddress()).isEqualTo(tender.getEmailAddress());
    assertThat(view.getAddedByOrganisationGroup()).isEqualTo(addedByPortalOrganisationGroup.getName());

    assertThat(view.getSummaryLinks()).extracting(SummaryLink::getLinkText).containsExactly(
        SummaryLinkText.EDIT.getDisplayName(),
        SummaryLinkText.CONVERT_TO_AWARDED_CONTRACT.getDisplayName(),
        SummaryLinkText.DELETE.getDisplayName()
    );
  }

  @Test
  public void canShowInTaskList_whenTaskValidForProjectDetail_thenTrue() {
    when(upcomingTenderService.isTaskValidForProjectDetail(details)).thenReturn(true);

    assertThat(upcomingTenderSummaryService.isTaskValidForProjectDetail(details)).isTrue();
  }

  @Test
  public void canShowInTaskList_whenTaskNotValidForProjectDetail_thenFalse() {
    when(upcomingTenderService.isTaskValidForProjectDetail(details)).thenReturn(false);

    assertThat(upcomingTenderSummaryService.isTaskValidForProjectDetail(details)).isFalse();
  }

  @Test
  public void getSummaryViews_withProjectAndVersion_whenFound_thenReturnPopulatedList() {

    final var upcomingTender = UpcomingTenderUtil.getUpcomingTender(details);

    final var project = details.getProject();
    final var version = details.getVersion();

    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        eq(upcomingTender.getProjectDetail()),
        any())
    ).thenReturn(true);
    when(upcomingTenderService.getUpcomingTendersForProjectVersion(project, version))
        .thenReturn(List.of(upcomingTender));

    final var result = upcomingTenderSummaryService.getSummaryViews(project, version);

    assertThat(result).hasSize(1);

    final var upcomingTenderView = result.get(0);

    assertThat(upcomingTenderView.getTenderFunction().getValue()).isEqualTo(upcomingTender.getTenderFunction().getDisplayName());
    assertThat(upcomingTenderView.getTenderFunction().getTag()).isEqualTo(Tag.NONE);
    checkCommonFields(upcomingTenderView, upcomingTender);

  }

  @Test
  public void getSummaryViews_withProjectAndVersion_whenNotFound_thenReturnEmptyList() {

    final var project = details.getProject();
    final var version = details.getVersion();

    when(upcomingTenderService.getUpcomingTendersForProjectVersion(project, version))
        .thenReturn(Collections.emptyList());

    final var result = upcomingTenderSummaryService.getSummaryViews(project, version);
    assertThat(result).isEmpty();
  }

  @Test
  public void getUpcomingTenderView_whenUserCanAccessService_thenThereAreLinks() {
    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        eq(upcomingTender.getProjectDetail()),
        any())
    ).thenReturn(true);
    var editLink = new SummaryLink(
        SummaryLinkText.EDIT.getDisplayName(),
        ReverseRouter.route(on(UpcomingTendersController.class).editUpcomingTender(
            upcomingTender.getProjectDetail().getProject().getId(),
            upcomingTender.getId(),
            null
        ))
    );

    var convertLink = new SummaryLink(
        SummaryLinkText.CONVERT_TO_AWARDED_CONTRACT.getDisplayName(),
        ReverseRouter.route(on(UpcomingTenderConversionController.class).convertUpcomingTenderConfirm(
            upcomingTender.getProjectDetail().getProject().getId(),
            upcomingTender.getId(),
            1,
            null
        ))
    );

    var removeLink = new SummaryLink(
        SummaryLinkText.DELETE.getDisplayName(),
        ReverseRouter.route(on(UpcomingTendersController.class).removeUpcomingTenderConfirm(
            upcomingTender.getProjectDetail().getProject().getId(),
            upcomingTender.getId(),
            1,
            null
        ))
    );

    var upcomingTenderView = upcomingTenderSummaryService.getUpcomingTenderView(upcomingTender, 1);

    assertThat(upcomingTenderView.getSummaryLinks()).containsExactly(editLink, convertLink, removeLink);
  }

  @Test
  public void getUpcomingTenderView_whenUserCannotAccessService_thenThereAreNoLinks() {
    var upcomingTenderView = upcomingTenderSummaryService.getUpcomingTenderView(upcomingTender, 1);

    assertThat(upcomingTenderView.getSummaryLinks()).isEmpty();
  }

  @Test
  public void getValidatedUpcomingTenderView() {
    var upcomingTenderView = upcomingTenderSummaryService.getUpcomingTenderView(upcomingTender, 1);

    assertThat(upcomingTenderView.isValid()).isTrue();
  }
}
