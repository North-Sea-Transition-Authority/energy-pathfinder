package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderController;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderConversionController;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender.ForwardWorkPlanUpcomingTenderView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanUpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanUpcomingTenderSummaryServiceTest {

  @Mock
  private ForwardWorkPlanUpcomingTenderService workPlanUpcomingTenderService;

  @Mock
  private ProjectSectionItemOwnershipService projectSectionItemOwnershipService;

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  private ForwardWorkPlanUpcomingTenderSummaryService workPlanUpcomingTenderSummaryService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);

  private final ForwardWorkPlanUpcomingTender workPlanUpcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);

  private final ForwardWorkPlanUpcomingTender manualEntryWorkPlanUpcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender_manualEntry(projectDetail);

  @Before
  public void setup() {
    workPlanUpcomingTenderSummaryService = new ForwardWorkPlanUpcomingTenderSummaryService(
        workPlanUpcomingTenderService,
        projectSectionItemOwnershipService,
        portalOrganisationAccessor
    );
    when(workPlanUpcomingTenderService.getUpcomingTendersForDetail(projectDetail)).thenReturn(
        List.of(workPlanUpcomingTender, manualEntryWorkPlanUpcomingTender)
    );
  }

  @Test
  public void getSummaryViews() {
    var views = workPlanUpcomingTenderSummaryService.getSummaryViews(projectDetail);
    checkCommonViewFields(views);
  }

  private void checkCommonViewFields(List<ForwardWorkPlanUpcomingTenderView> views) {
    assertThat(views.size()).isEqualTo(2);
    var view1 = views.get(0);
    var view2 = views.get(1);
    var view1TenderDepartment = view1.getTenderDepartment();
    var view2TenderDepartment = view2.getTenderDepartment();
    assertThat(view1TenderDepartment.getValue()).isEqualTo(workPlanUpcomingTender.getDepartmentType().getDisplayName());
    assertThat(view1TenderDepartment.getTag()).isEqualTo(Tag.NONE);
    assertThat(view1.getDisplayOrder()).isEqualTo(1);
    assertThat(view2TenderDepartment.getValue()).isEqualTo(manualEntryWorkPlanUpcomingTender.getManualDepartmentType());
    assertThat(view2TenderDepartment.getTag()).isEqualTo(Tag.NOT_FROM_LIST);
    assertThat(view2.getDisplayOrder()).isEqualTo(2);
    checkCommonFields(view1, workPlanUpcomingTender);
    checkCommonFields(view2, manualEntryWorkPlanUpcomingTender);
  }
  @Test
  public void getSummaryViews_withProjectAndVersion_whenTendersFound_thenReturnPopulatedList() {

    final var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);

    when(workPlanUpcomingTenderService.getUpcomingTendersForProjectAndVersion(projectDetail.getProject(), projectDetail.getVersion()))
        .thenReturn(List.of(upcomingTender));

    final var result = workPlanUpcomingTenderSummaryService.getSummaryViews(projectDetail.getProject(), projectDetail.getVersion());

    assertThat(result).hasSize(1);

    final var upcomingTenderView = result.get(0);

    assertThat(upcomingTenderView.getTenderDepartment().getValue()).isEqualTo(upcomingTender.getDepartmentType().getDisplayName());
    assertThat(upcomingTenderView.getTenderDepartment().getTag()).isEqualTo(Tag.NONE);
    checkCommonFields(upcomingTenderView, upcomingTender);
  }

  @Test
  public void getSummaryViews_withProjectDetail_whenTendersFound_thenReturnPopulatedList() {

    final var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);

    when(workPlanUpcomingTenderService.getUpcomingTendersForDetail(projectDetail))
        .thenReturn(List.of(upcomingTender));

    final var result = workPlanUpcomingTenderSummaryService.getSummaryViews(projectDetail);

    assertThat(result).hasSize(1);

    final var upcomingTenderView = result.get(0);

    assertThat(upcomingTenderView.getTenderDepartment().getValue()).isEqualTo(upcomingTender.getDepartmentType().getDisplayName());
    assertThat(upcomingTenderView.getTenderDepartment().getTag()).isEqualTo(Tag.NONE);
    checkCommonFields(upcomingTenderView, upcomingTender);
  }

  @Test
  public void getSummaryViews_withProjectDetail_whenNoTendersFound_thenReturnEmptyList() {

    when(workPlanUpcomingTenderService.getUpcomingTendersForDetail(projectDetail))
        .thenReturn(Collections.emptyList());

    final var result = workPlanUpcomingTenderSummaryService.getSummaryViews(projectDetail);
    assertThat(result).isEmpty();
  }

  @Test
  public void getSummaryViews_withProjectAndVersion_whenNoTendersFound_thenReturnEmptyList() {

    when(workPlanUpcomingTenderService.getUpcomingTendersForProjectAndVersion(projectDetail.getProject(), projectDetail.getVersion()))
        .thenReturn(Collections.emptyList());

    final var result = workPlanUpcomingTenderSummaryService.getSummaryViews(projectDetail.getProject(), projectDetail.getVersion());
    assertThat(result).isEmpty();
  }

  private void checkCommonFields(ForwardWorkPlanUpcomingTenderView view, ForwardWorkPlanUpcomingTender tender) {
    assertThat(view.getDescriptionOfWork()).isEqualTo(tender.getDescriptionOfWork());
    assertThat(view.getEstimatedTenderStartDate()).isEqualTo(DateUtil.getDateFromQuarterYear(tender.getEstimatedTenderDateQuarter(), tender.getEstimatedTenderDateYear()));
    assertThat(view.getContractBand()).isEqualTo(tender.getContractBand().getDisplayName());

    assertThat(view.getContactName()).isEqualTo(tender.getContactName());
    assertThat(view.getContactPhoneNumber()).isEqualTo(tender.getPhoneNumber());
    assertThat(view.getContactJobTitle()).isEqualTo(tender.getJobTitle());
    assertThat(view.getContactEmailAddress()).isEqualTo(tender.getEmailAddress());
  }

  @Test
  public void getValidatedSummaryViews_allValid() {
    when(workPlanUpcomingTenderService.isValid(any(), any())).thenReturn(true);
    var views = workPlanUpcomingTenderSummaryService.getValidatedSummaryViews(projectDetail);
    assertThat(views.size()).isEqualTo(2);
    var view1 = views.get(0);
    var view2 = views.get(1);
    assertThat(view1.isValid()).isTrue();
    assertThat(view2.isValid()).isTrue();
  }

  @Test
  public void getValidatedSummaryViews_containsInvalidEntry() {
    when(workPlanUpcomingTenderService.isValid(workPlanUpcomingTender, ValidationType.FULL)).thenReturn(true);
    var views = workPlanUpcomingTenderSummaryService.getValidatedSummaryViews(projectDetail);
    assertThat(views.size()).isEqualTo(2);
    var view1 = views.get(0);
    var view2 = views.get(1);
    assertThat(view1.isValid()).isTrue();
    assertThat(view2.isValid()).isFalse();
  }

  @Test
  public void getValidatedUpcomingTenderView() {
    when(workPlanUpcomingTenderService.isValid(workPlanUpcomingTender, ValidationType.FULL)).thenReturn(true);

    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        eq(workPlanUpcomingTender.getProjectDetail()),
        any())
    ).thenReturn(true);

    var upcomingTenderView = workPlanUpcomingTenderSummaryService.getUpcomingTenderView(workPlanUpcomingTender, 1);

    assertThat(upcomingTenderView.isValid()).isTrue();
  }

  @Test
  public void getUpcomingTenderView() {
    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        eq(workPlanUpcomingTender.getProjectDetail()),
        any())
    ).thenReturn(true);

    var editLink = new SummaryLink(
        SummaryLinkText.EDIT.getDisplayName(),
        ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class).editUpcomingTender(
            workPlanUpcomingTender.getProjectDetail().getProject().getId(),
            workPlanUpcomingTender.getId(),
            null
        ))
    );

    var convertLink = new SummaryLink(
        SummaryLinkText.CONVERT_TO_AWARDED_CONTRACT.getDisplayName(),
        ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderConversionController.class).convertUpcomingTenderConfirm(
            workPlanUpcomingTender.getProjectDetail().getProject().getId(),
            workPlanUpcomingTender.getId(),
            1,
            null
        ))
    );

    var removeLink = new SummaryLink(
        SummaryLinkText.DELETE.getDisplayName(),
        ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class).removeUpcomingTenderConfirm(
            workPlanUpcomingTender.getProjectDetail().getProject().getId(),
            workPlanUpcomingTender.getId(),
            1,
            null
        ))
    );

    var upcomingTenderView = workPlanUpcomingTenderSummaryService.getUpcomingTenderView(workPlanUpcomingTender, 1);

    assertThat(upcomingTenderView.getSummaryLinks()).containsExactly(editLink, convertLink, removeLink);
  }
}
