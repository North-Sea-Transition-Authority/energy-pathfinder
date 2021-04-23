package uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.WorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.WorkPlanUpcomingTenderContractBand;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.WorkPlanUpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class WorkPlanUpcomingTenderViewUtilTest {

  private ProjectDetail projectDetail;

  @Before
  public void setup() {
    projectDetail = ProjectUtil.getProjectDetails();
  }

  @Test
  public void createUpcomingTenderView_whenNoTenderDepartment_thenEmptyStringWithTag() {

    var upcomingTender = WorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setDepartmentType(null);
    upcomingTender.setManualDepartmentType(null);

    final var displayOrder = 1;

    final var upcomingTenderView = WorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(
        upcomingTender,
        displayOrder
    );

    assertThat(upcomingTenderView.getTenderDepartment()).isEqualTo(new StringWithTag());

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  public void createUpComingTenderView_whenFromListTenderFunction_thenFromListStringWithTag() {

    final var tenderFunction = Function.DRILLING;

    var upcomingTender = WorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setDepartmentType(tenderFunction);
    upcomingTender.setManualDepartmentType(null);

    final var displayOrder = 2;

    final var upcomingTenderView = WorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(
        upcomingTender,
        displayOrder
    );

    assertThat(upcomingTenderView.getTenderDepartment()).isEqualTo(
        new StringWithTag(tenderFunction.getDisplayName(), Tag.NONE)
    );

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  public void createUpComingTenderView_whenNotFromListTenderFunction_thenNotFromListStringWithTag() {

    final var tenderFunction = "Manual entry";

    var upcomingTender = WorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setDepartmentType(null);
    upcomingTender.setManualDepartmentType(tenderFunction);

    final var displayOrder = 2;

    final var upcomingTenderView = WorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(
        upcomingTender,
        displayOrder
    );

    assertThat(upcomingTenderView.getTenderDepartment()).isEqualTo(
        new StringWithTag(tenderFunction, Tag.NOT_FROM_LIST)
    );

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  public void createUpComingTenderView_whenContractBandProvided_thenContractBandPopulated() {

    final var contractBand = WorkPlanUpcomingTenderContractBand.GREATER_THAN_OR_EQUAL_TO_5M;

    var upcomingTender = WorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setContractBand(contractBand);

    final var displayOrder = 2;

    final var upcomingTenderView = WorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(
        upcomingTender,
        displayOrder
    );

    assertThat(upcomingTenderView.getContractBand()).isEqualTo(contractBand.getDisplayName());

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  public void createUpComingTenderView_whenContractBandNotProvided_thenEmptyString() {

    var upcomingTender = WorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setContractBand(null);

    final var displayOrder = 2;

    final var upcomingTenderView = WorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(
        upcomingTender,
        displayOrder
    );

    assertThat(upcomingTenderView.getContractBand()).isEmpty();

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  public void createUpComingTenderView_whenIsValidTrue_thenIsValidTrueInView() {

    var upcomingTender = WorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);

    final var displayOrder = 3;
    final var isValid = true;

    final var upcomingTenderView = WorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(
        upcomingTender,
        displayOrder,
        isValid
    );

    assertThat(upcomingTenderView.isValid()).isTrue();

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  public void createUpComingTenderView_whenIsValidFalse_thenIsValidFalseInView() {

    var upcomingTender = WorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);

    final var displayOrder = 3;
    final var isValid = false;

    final var upcomingTenderView = WorkPlanUpcomingTenderViewUtil.createUpcomingTenderView(
        upcomingTender,
        displayOrder,
        isValid
    );

    assertThat(upcomingTenderView.isValid()).isFalse();

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  private void assertCommonProperties(
      WorkPlanUpcomingTenderView upcomingTenderView,
      WorkPlanUpcomingTender upcomingTender,
      int displayOrder
  ) {
    assertThat(upcomingTenderView.getDescriptionOfWork()).isEqualTo(upcomingTender.getDescriptionOfWork());
    assertThat(upcomingTenderView.getEstimatedTenderDate()).isEqualTo(DateUtil.formatDate(upcomingTender.getEstimatedTenderDate()));
    assertThat(upcomingTenderView.getContactName()).isEqualTo(upcomingTender.getName());
    assertThat(upcomingTenderView.getContactPhoneNumber()).isEqualTo(upcomingTender.getPhoneNumber());
    assertThat(upcomingTenderView.getContactEmailAddress()).isEqualTo(upcomingTender.getEmailAddress());
    assertThat(upcomingTenderView.getContactJobTitle()).isEqualTo(upcomingTender.getJobTitle());
    assertThat(upcomingTenderView.getProjectId()).isEqualTo(upcomingTender.getProjectDetail().getProject().getId());
    assertThat(upcomingTenderView.getDisplayOrder()).isEqualTo(displayOrder);
  }
}
