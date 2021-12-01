package uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.duration.DurationPeriod;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.WorkPlanUpcomingTenderContractBand;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanUpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanUpcomingTenderViewUtilTest {

  private ProjectDetail projectDetail;

  @Before
  public void setup() {
    projectDetail = ProjectUtil.getProjectDetails();
  }

  @Test
  public void createUpcomingTenderView_whenNoTenderDepartment_thenEmptyStringWithTag() {

    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setDepartmentType(null);
    upcomingTender.setManualDepartmentType(null);

    final var displayOrder = 1;

    final var upcomingTenderView = ForwardWorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(
        upcomingTender,
        displayOrder
    );

    assertThat(upcomingTenderView.getTenderDepartment()).isEqualTo(new StringWithTag());

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  public void createUpComingTenderView_whenFromListTenderDepartment_thenFromListStringWithTag() {

    final var tenderDepartment = Function.DRILLING;

    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setDepartmentType(tenderDepartment);
    upcomingTender.setManualDepartmentType(null);

    final var displayOrder = 2;

    final var upcomingTenderView = ForwardWorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(
        upcomingTender,
        displayOrder
    );

    assertThat(upcomingTenderView.getTenderDepartment()).isEqualTo(
        new StringWithTag(tenderDepartment.getDisplayName(), Tag.NONE)
    );

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  public void createUpComingTenderView_whenNotFromListTenderDepartment_thenNotFromListStringWithTag() {

    final var tenderDepartment = "Manual entry";

    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setDepartmentType(null);
    upcomingTender.setManualDepartmentType(tenderDepartment);

    final var displayOrder = 2;

    final var upcomingTenderView = ForwardWorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(
        upcomingTender,
        displayOrder
    );

    assertThat(upcomingTenderView.getTenderDepartment()).isEqualTo(
        new StringWithTag(tenderDepartment, Tag.NOT_FROM_LIST)
    );

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  public void createUpComingTenderView_whenContractBandProvided_thenContractBandPopulated() {

    final var contractBand = WorkPlanUpcomingTenderContractBand.GREATER_THAN_OR_EQUAL_TO_5M;

    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setContractBand(contractBand);

    final var displayOrder = 2;

    final var upcomingTenderView = ForwardWorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(
        upcomingTender,
        displayOrder
    );

    assertThat(upcomingTenderView.getContractBand()).isEqualTo(contractBand.getDisplayName());

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  public void createUpComingTenderView_whenContractBandNotProvided_thenEmptyString() {

    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setContractBand(null);

    final var displayOrder = 2;

    final var upcomingTenderView = ForwardWorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(
        upcomingTender,
        displayOrder
    );

    assertThat(upcomingTenderView.getContractBand()).isEmpty();

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  public void createUpComingTenderView_whenIsValidTrue_thenIsValidTrueInView() {

    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);

    final var displayOrder = 3;
    final var isValid = true;

    final var upcomingTenderView = ForwardWorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(
        upcomingTender,
        displayOrder,
        isValid
    );

    assertThat(upcomingTenderView.isValid()).isTrue();

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  public void createUpComingTenderView_whenIsValidFalse_thenIsValidFalseInView() {

    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);

    final var displayOrder = 3;
    final var isValid = false;

    final var upcomingTenderView = ForwardWorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(
        upcomingTender,
        displayOrder,
        isValid
    );

    assertThat(upcomingTenderView.isValid()).isFalse();

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  public void createUpcomingTenderView_whenContractTermDurationAndPeriodNull_thenContractLengthSetToEmptyString() {

    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setContractTermDurationPeriod(null);
    upcomingTender.setContractTermDuration(null);

    final var displayOrder = 1;

    final var upcomingTenderView = ForwardWorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(
        upcomingTender,
        displayOrder
    );

    assertThat(upcomingTenderView.getContractLength()).isEmpty();
    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  public void createUpcomingTenderView_whenContractTermDurationPeriodNull_thenContractLengthSetToEmptyString() {

    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setContractTermDurationPeriod(null);
    upcomingTender.setContractTermDuration(10);

    final var displayOrder = 1;

    final var upcomingTenderView = ForwardWorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(
        upcomingTender,
        displayOrder
    );

    assertThat(upcomingTenderView.getContractLength()).isEmpty();
    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  public void createUpcomingTenderView_whenContractTermDurationNull_thenContractLengthSetToEmptyString() {

    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setContractTermDurationPeriod(DurationPeriod.DAYS);
    upcomingTender.setContractTermDuration(null);

    final var displayOrder = 1;

    final var upcomingTenderView = ForwardWorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(
        upcomingTender,
        displayOrder
    );

    assertThat(upcomingTenderView.getContractLength()).isEmpty();
    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  public void createUpcomingTenderView_whenContractTermDurationAbove1AndPeriodProvided_assertPluralContractLength() {

    final var contractTermDuration = 10;
    final var contractDurationPeriod = DurationPeriod.WEEKS;
    final var expectedContractLength = String.format(
        "%s %s",
        contractTermDuration,
        contractDurationPeriod.getDisplayNamePlural().toLowerCase()
    );

    assertExpectedContractTerm(contractTermDuration, contractDurationPeriod, expectedContractLength);
  }

  @Test
  public void createUpcomingTenderView_whenContractTermDurationEqual1AndPeriodProvided_assertSingularContractLength() {

    final var contractTermDuration = 1;
    final var contractDurationPeriod = DurationPeriod.DAYS;
    final var expectedContractLength = String.format(
        "%s %s",
        contractTermDuration,
        contractDurationPeriod.getDisplayNameSingular().toLowerCase()
    );

    assertExpectedContractTerm(contractTermDuration, contractDurationPeriod, expectedContractLength);
  }

  private void assertExpectedContractTerm(Integer contractTermDuration,
                                          DurationPeriod contractTermDurationPeriod,
                                          String expectedContractTermString) {

    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setContractTermDurationPeriod(contractTermDurationPeriod);
    upcomingTender.setContractTermDuration(contractTermDuration);

    final var displayOrder = 1;

    final var upcomingTenderView = ForwardWorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(
        upcomingTender,
        displayOrder
    );

    assertThat(upcomingTenderView.getContractLength()).isEqualTo(expectedContractTermString);
    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  private void assertCommonProperties(
      ForwardWorkPlanUpcomingTenderView upcomingTenderView,
      ForwardWorkPlanUpcomingTender upcomingTender,
      int displayOrder
  ) {
    assertThat(upcomingTenderView.getDescriptionOfWork()).isEqualTo(upcomingTender.getDescriptionOfWork());
    assertThat(upcomingTenderView.getEstimatedTenderStartDate()).isEqualTo(DateUtil.getDateFromQuarterYear(upcomingTender.getEstimatedTenderDateQuarter(), upcomingTender.getEstimatedTenderDateYear()));
    assertThat(upcomingTenderView.getContactName()).isEqualTo(upcomingTender.getName());
    assertThat(upcomingTenderView.getContactPhoneNumber()).isEqualTo(upcomingTender.getPhoneNumber());
    assertThat(upcomingTenderView.getContactEmailAddress()).isEqualTo(upcomingTender.getEmailAddress());
    assertThat(upcomingTenderView.getContactJobTitle()).isEqualTo(upcomingTender.getJobTitle());
    assertThat(upcomingTenderView.getProjectId()).isEqualTo(upcomingTender.getProjectDetail().getProject().getId());
    assertThat(upcomingTenderView.getDisplayOrder()).isEqualTo(displayOrder);

    final var editSummaryLink = new SummaryLink(
        SummaryLinkText.EDIT.getDisplayName(),
        ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class).editUpcomingTender(
            upcomingTenderView.getProjectId(),
            upcomingTenderView.getId(),
            null
        ))
    );

    final var removeSummaryLink = new SummaryLink(
        SummaryLinkText.DELETE.getDisplayName(),
        ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class).removeUpcomingTenderConfirm(
            upcomingTenderView.getProjectId(),
            upcomingTenderView.getId(),
            displayOrder,
            null
        ))
    );

    assertThat(upcomingTenderView.getSummaryLinks()).containsExactly(editSummaryLink, removeSummaryLink);
  }
}
