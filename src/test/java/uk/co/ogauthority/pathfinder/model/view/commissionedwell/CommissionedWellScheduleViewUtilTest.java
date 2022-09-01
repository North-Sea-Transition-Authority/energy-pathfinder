package uk.co.ogauthority.pathfinder.model.view.commissionedwell;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.controller.project.commissionedwell.CommissionedWellController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWell;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWellSchedule;
import uk.co.ogauthority.pathfinder.model.entity.wellbore.Wellbore;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.WellboreTestUtil;

@ExtendWith(MockitoExtension.class)
class CommissionedWellScheduleViewUtilTest {

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  @Test
  void from_whenCommissioningPeriodProvided_verifyExpectedProperties() {

    var commissionedWellSchedule = getCompleteCommissionedWellSchedule();
    commissionedWellSchedule.setEarliestStartYear(2021);
    commissionedWellSchedule.setLatestCompletionYear(LocalDate.now().getYear());

    var displayOrder = 1;

    var resultingView = CommissionedWellScheduleViewUtil.from(
        commissionedWellSchedule,
        List.of(),
        1
    );

    assertCommonProperties(resultingView, displayOrder);

    assertThat(resultingView.getEarliestStartYear()).isEqualTo(
        String.format("%s: %s", CommissionedWellScheduleViewUtil.EARLIEST_START_YEAR_TEXT, commissionedWellSchedule.getEarliestStartYear())
    );
    assertThat(resultingView.getLatestCompletionYear()).isEqualTo(
        String.format("%s: %s", CommissionedWellScheduleViewUtil.LATEST_COMPLETION_YEAR_TEXT, commissionedWellSchedule.getLatestCompletionYear())
    );
  }

  @Test
  void from_whenNoCommissioningPeriodProvided_verifyDefaultValue() {

    var commissionedWellSchedule = getCompleteCommissionedWellSchedule();
    commissionedWellSchedule.setEarliestStartYear(null);
    commissionedWellSchedule.setLatestCompletionYear(null);

    var displayOrder = 2;

    var resultingView = CommissionedWellScheduleViewUtil.from(
        commissionedWellSchedule,
        List.of(),
        displayOrder
    );

    assertCommonProperties(resultingView, displayOrder);

    assertThat(resultingView.getEarliestStartYear()).isEqualTo(
        String.format("%s: %s", CommissionedWellScheduleViewUtil.EARLIEST_START_YEAR_TEXT, CommissionedWellScheduleViewUtil.DEFAULT_YEAR_TEXT)
    );
    assertThat(resultingView.getLatestCompletionYear()).isEqualTo(
        String.format("%s: %s", CommissionedWellScheduleViewUtil.LATEST_COMPLETION_YEAR_TEXT, CommissionedWellScheduleViewUtil.DEFAULT_YEAR_TEXT)
    );
  }

  @Test
  void from_whenNoWellsToCommissioning_thenEmptyWellList() {

    var commissionedWellSchedule = getCompleteCommissionedWellSchedule();

    var emptyCommissionedWellList = new ArrayList<CommissionedWell>();

    var displayOrder = 3;

    var resultingView = CommissionedWellScheduleViewUtil.from(
        commissionedWellSchedule,
        emptyCommissionedWellList,
        displayOrder
    );

    assertCommonProperties(resultingView, displayOrder);

    assertThat(resultingView.getWells()).isEmpty();
  }

  @Test
  void from_whenWellsToCommissioning_thenPopulatedWellList() {

    var commissionedWellSchedule = getCompleteCommissionedWellSchedule();

    var commissionedWell = getCompleteCommissionedWell(commissionedWellSchedule);

    var commissionedWellList = List.of(commissionedWell);

    var displayOrder = 3;

    var resultingView = CommissionedWellScheduleViewUtil.from(
        commissionedWellSchedule,
        commissionedWellList,
        displayOrder
    );

    var expectedWellboreList = commissionedWellList
        .stream()
        .map(wellbore -> wellbore.getWellbore().getRegistrationNo())
        .collect(Collectors.toUnmodifiableList());

    assertCommonProperties(resultingView, displayOrder);

    assertThat(resultingView.getWells()).isEqualTo(expectedWellboreList);
  }

  @Test
  void from_whenWellsToCommissioning_thenVerifySortOrder() {

    var commissionedWellSchedule = getCompleteCommissionedWellSchedule();

    var firstWellboreAlphabetically = getWellbore(1, "a");
    var secondWellboreAlphabetically = getWellbore(2, "b");

    var firstCommissionedWellAlphabetically = getCompleteCommissionedWell(commissionedWellSchedule);
    firstCommissionedWellAlphabetically.setWellbore(firstWellboreAlphabetically);

    var secondCommissionedWellAlphabetically = getCompleteCommissionedWell(commissionedWellSchedule);
    secondCommissionedWellAlphabetically.setWellbore(secondWellboreAlphabetically);

    var commissionedWellList = List.of(
        secondCommissionedWellAlphabetically,
        firstCommissionedWellAlphabetically
    );

    var displayOrder = 4;

    var resultingView = CommissionedWellScheduleViewUtil.from(
        commissionedWellSchedule,
        commissionedWellList,
        displayOrder
    );

    assertCommonProperties(resultingView, displayOrder);

    assertThat(resultingView.getWells()).containsExactly(
        firstWellboreAlphabetically.getRegistrationNo(),
        secondWellboreAlphabetically.getRegistrationNo()
    );
  }

  @Test
  void from_verifyLinks() {

    var commissionedWellSchedule = getCompleteCommissionedWellSchedule();

    var displayOrder = 5;

    var resultingView = CommissionedWellScheduleViewUtil.from(
        commissionedWellSchedule,
        List.of(),
        displayOrder
    );

    assertCommonProperties(resultingView, displayOrder);

    assertThat(resultingView.getSummaryLinks()).containsExactly(
        new SummaryLink(
            SummaryLinkText.EDIT.getDisplayName(),
            ReverseRouter.route(on(CommissionedWellController.class).getCommissionedWellSchedule(
                projectDetail.getProject().getId(),
                commissionedWellSchedule.getId(),
                null
            ))
        ),
        new SummaryLink(
            SummaryLinkText.DELETE.getDisplayName(),
            ReverseRouter.route(on(CommissionedWellController.class).removeCommissionedWellScheduleConfirmation(
                projectDetail.getProject().getId(),
                commissionedWellSchedule.getId(),
                displayOrder,
                null
            ))
        )
    );
  }

  @Test
  void from_whenIsValidIsTrue_thenVerifyValidPropertyIsTrue() {

    var commissionedWellSchedule = getCompleteCommissionedWellSchedule();

    var displayOrder = 6;

    var resultingView = CommissionedWellScheduleViewUtil.from(
        commissionedWellSchedule,
        List.of(),
        displayOrder,
        true
    );

    assertCommonProperties(resultingView, displayOrder);
    assertThat(resultingView.isValid()).isTrue();
  }

  @Test
  void from_whenIsValidIsFalse_thenVerifyValidPropertyIsFalse() {

    var commissionedWellSchedule = getCompleteCommissionedWellSchedule();

    var displayOrder = 6;

    var resultingView = CommissionedWellScheduleViewUtil.from(
        commissionedWellSchedule,
        List.of(),
        displayOrder,
        false
    );

    assertCommonProperties(resultingView, displayOrder);
    assertThat(resultingView.isValid()).isFalse();

  }

  @Test
  void from_whenIsValidNotProvided_thenVerifyValidPropertyIsTrue() {

    var commissionedWellSchedule = getCompleteCommissionedWellSchedule();

    var displayOrder = 6;

    var resultingView = CommissionedWellScheduleViewUtil.from(
        commissionedWellSchedule,
        List.of(),
        displayOrder
    );

    assertCommonProperties(resultingView, displayOrder);
    assertThat(resultingView.isValid()).isTrue();

  }

  private CommissionedWellSchedule getCompleteCommissionedWellSchedule() {
    var commissionedWellSchedule = new CommissionedWellSchedule();
    commissionedWellSchedule.setProjectDetail(projectDetail);
    commissionedWellSchedule.setEarliestStartYear(2021);
    commissionedWellSchedule.setLatestCompletionYear(LocalDate.now().getYear());
    return commissionedWellSchedule;
  }

  private CommissionedWell getCompleteCommissionedWell(CommissionedWellSchedule commissionedWellSchedule) {
    var commissionedWell = new CommissionedWell();
    commissionedWell.setCommissionedWellSchedule(commissionedWellSchedule);
    commissionedWell.setWellbore(WellboreTestUtil.createWellbore());
    return commissionedWell;
  }

  private Wellbore getWellbore(int id, String quadrantNo) {
    return WellboreTestUtil.createWellbore(
        id,
        String.format("%s%s", id, quadrantNo),
        quadrantNo,
        "1",
        "b",
        "c",
        "1",
        "d"
    );
  }

  private void assertCommonProperties(CommissionedWellScheduleView commissionedWellScheduleView, int displayOrder) {
    assertThat(commissionedWellScheduleView.getDisplayOrder()).isEqualTo(displayOrder);
  }

}