package uk.co.ogauthority.pathfinder.model.view.plugabandonmentschedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.project.plugabandonmentschedule.PlugAbandonmentScheduleController;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentSchedule;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentWell;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.testutil.PlugAbandonmentScheduleTestUtil;
import uk.co.ogauthority.pathfinder.testutil.PlugAbandonmentWellTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class PlugAbandonmentScheduleViewUtilTest {

  private static final Integer DISPLAY_ORDER = 1;
  private static final boolean IS_VALID = true;

  @Test
  public void from_withDecommissioningPeriodProvided() {

    var plugAbandonmentSchedule = PlugAbandonmentScheduleTestUtil.createPlugAbandonmentSchedule();
    plugAbandonmentSchedule.setEarliestStartYear(2019);
    plugAbandonmentSchedule.setLatestCompletionYear(2020);
    var plugAbandonmentWells = List.of(
        PlugAbandonmentWellTestUtil.createPlugAbandonmentWell(),
        PlugAbandonmentWellTestUtil.createPlugAbandonmentWell()
    );

    var plugAbandonmentScheduleView = PlugAbandonmentScheduleViewUtil.from(
        plugAbandonmentSchedule,
        plugAbandonmentWells,
        DISPLAY_ORDER,
        IS_VALID
    );

    assertThat(plugAbandonmentScheduleView.getEarliestStartYear()).isEqualTo(
        String.format(PlugAbandonmentScheduleViewUtil.EARLIEST_START_YEAR_TEXT, plugAbandonmentSchedule.getEarliestStartYear())
    );
    assertThat(plugAbandonmentScheduleView.getLatestCompletionYear()).isEqualTo(
        String.format(PlugAbandonmentScheduleViewUtil.LATEST_COMPLETION_YEAR_TEXT, plugAbandonmentSchedule.getLatestCompletionYear())
    );

    checkCommonFields(plugAbandonmentScheduleView, plugAbandonmentSchedule, plugAbandonmentWells, DISPLAY_ORDER, IS_VALID);
  }

  @Test
  public void from_withDecommissioningPeriodEmpty() {

    var plugAbandonmentSchedule = PlugAbandonmentScheduleTestUtil.createPlugAbandonmentSchedule();
    plugAbandonmentSchedule.setEarliestStartYear(null);
    plugAbandonmentSchedule.setLatestCompletionYear(null);
    var plugAbandonmentWells = List.of(
        PlugAbandonmentWellTestUtil.createPlugAbandonmentWell(),
        PlugAbandonmentWellTestUtil.createPlugAbandonmentWell()
    );

    var plugAbandonmentScheduleView = PlugAbandonmentScheduleViewUtil.from(
        plugAbandonmentSchedule,
        plugAbandonmentWells,
        DISPLAY_ORDER,
        IS_VALID
    );

    assertThat(plugAbandonmentScheduleView.getEarliestStartYear()).isEqualTo(
        String.format(PlugAbandonmentScheduleViewUtil.EARLIEST_START_YEAR_TEXT, PlugAbandonmentScheduleViewUtil.DEFAULT_YEAR_TEXT)
    );
    assertThat(plugAbandonmentScheduleView.getLatestCompletionYear()).isEqualTo(
        String.format(PlugAbandonmentScheduleViewUtil.LATEST_COMPLETION_YEAR_TEXT, PlugAbandonmentScheduleViewUtil.DEFAULT_YEAR_TEXT)
    );

    checkCommonFields(plugAbandonmentScheduleView, plugAbandonmentSchedule, plugAbandonmentWells, DISPLAY_ORDER, IS_VALID);
  }

  @Test
  public void from_whenIsValidTrue_thenIsValidInViewTrue() {
    final var plugAbandonmentSchedule = PlugAbandonmentScheduleTestUtil.createPlugAbandonmentSchedule();
    final var plugAbandonmentWells = List.of(
        PlugAbandonmentWellTestUtil.createPlugAbandonmentWell(),
        PlugAbandonmentWellTestUtil.createPlugAbandonmentWell()
    );
    final var isValid = IS_VALID;
    final var plugAbandonmentScheduleView = PlugAbandonmentScheduleViewUtil.from(
        plugAbandonmentSchedule,
        plugAbandonmentWells,
        DISPLAY_ORDER,
        isValid
    );
    checkCommonFields(plugAbandonmentScheduleView, plugAbandonmentSchedule, plugAbandonmentWells, DISPLAY_ORDER, isValid);
  }

  @Test
  public void from_whenIsValidFalse_thenIsValidInViewFalse() {
    final var plugAbandonmentSchedule = PlugAbandonmentScheduleTestUtil.createPlugAbandonmentSchedule();
    final var plugAbandonmentWells = List.of(
        PlugAbandonmentWellTestUtil.createPlugAbandonmentWell(),
        PlugAbandonmentWellTestUtil.createPlugAbandonmentWell()
    );
    final var isValid = false;
    final var plugAbandonmentScheduleView = PlugAbandonmentScheduleViewUtil.from(
        plugAbandonmentSchedule,
        plugAbandonmentWells,
        DISPLAY_ORDER,
        isValid
    );
    checkCommonFields(plugAbandonmentScheduleView, plugAbandonmentSchedule, plugAbandonmentWells, DISPLAY_ORDER, isValid);
  }

  private void checkCommonFields(PlugAbandonmentScheduleView plugAbandonmentScheduleView,
                                 PlugAbandonmentSchedule plugAbandonmentSchedule,
                                 List<PlugAbandonmentWell> plugAbandonmentWells,
                                 Integer displayOrder,
                                 boolean isValid) {
    assertThat(plugAbandonmentScheduleView.getId()).isEqualTo(plugAbandonmentSchedule.getId());
    assertThat(plugAbandonmentScheduleView.getDisplayOrder()).isEqualTo(displayOrder);
    assertThat(plugAbandonmentScheduleView.getWells()).isEqualTo(plugAbandonmentWells.stream()
        .map(plugAbandonmentWell -> plugAbandonmentWell.getWellbore().getRegistrationNo())
        .collect(Collectors.toList()));
    assertThat(plugAbandonmentScheduleView.getValid()).isEqualTo(isValid);

    var editLink = new SummaryLink(
        SummaryLinkText.EDIT.getDisplayName(),
        ReverseRouter.route(on(PlugAbandonmentScheduleController.class).getPlugAbandonmentSchedule(
            plugAbandonmentSchedule.getProjectDetail().getProject().getId(),
            plugAbandonmentSchedule.getId(),
            null
        ))
    );

    var removeLink = new SummaryLink(
        SummaryLinkText.DELETE.getDisplayName(),
        ReverseRouter.route(on(PlugAbandonmentScheduleController.class).removePlugAbandonmentSchedule(
            plugAbandonmentSchedule.getProjectDetail().getProject().getId(),
            plugAbandonmentSchedule.getId(),
            displayOrder,
            null
        ))
    );

    assertThat(plugAbandonmentScheduleView.getSummaryLinks()).containsExactly(editLink, removeLink);

  }
}
