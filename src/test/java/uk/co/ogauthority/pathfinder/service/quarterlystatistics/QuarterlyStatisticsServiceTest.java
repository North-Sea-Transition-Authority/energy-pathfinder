package uk.co.ogauthority.pathfinder.service.quarterlystatistics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.quarterlystatistics.QuarterlyStatisticsController;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.testutil.ReportableProjectTestUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class QuarterlyStatisticsServiceTest {

  @Mock
  private ReportableProjectService reportableProjectService;

  private QuarterlyStatisticsService quarterlyStatisticsService;

  @Before
  public void setup() {
    quarterlyStatisticsService = new QuarterlyStatisticsService(reportableProjectService);
  }

  @Test
  public void getQuarterlyStatisticsByFieldStage_whenNoReportableProjects_allFieldStagesReturnedWithZeroValues() {

    final List<ReportableProjectView> reportableProjects = Collections.emptyList();

    final var statistics = quarterlyStatisticsService.getQuarterlyStatisticsByFieldStage(
        reportableProjects
    );

    final var fieldStagesFromStatistics = statistics
        .stream()
        .map(FieldStageStatistic::getFieldStage)
        .collect(Collectors.toList());

    final var fieldStagesFromEnum = Arrays.stream(FieldStage.values())
        .map(FieldStage::getDisplayName)
        .collect(Collectors.toList());

    // check that all the field stages are present in the list returned from getQuarterlyStatisticsByFieldStage
    assertThat(fieldStagesFromEnum).containsExactlyElementsOf(fieldStagesFromStatistics);

    var statistic = statistics.get(0);

    assertThat(statistic.getTotalProjects()).isZero();
    assertThat(statistic.getTotalProjectsUpdateThisQuarter()).isZero();
    assertThat(statistic.getPercentageOfProjectsUpdated()).isZero();
  }

  @Test
  public void getQuarterlyStatisticsByFieldStage_assertProjectTotalIsCorrect() {

    final var reportableProjectView1 = ReportableProjectTestUtil.createReportableProjectView(FieldStage.DEVELOPMENT);
    final var reportableProjectView2 = ReportableProjectTestUtil.createReportableProjectView(FieldStage.DEVELOPMENT);
    final var reportableProjectView3 = ReportableProjectTestUtil.createReportableProjectView(FieldStage.DECOMMISSIONING);

    final var reportableProjectViews = List.of(
        reportableProjectView1,
        reportableProjectView2,
        reportableProjectView3
    );

    final var statistics = quarterlyStatisticsService.getQuarterlyStatisticsByFieldStage(
        reportableProjectViews
    );

    assertThat(getFieldStageStatistic(statistics, FieldStage.DEVELOPMENT).getTotalProjects()).isEqualTo(2);
    assertThat(getFieldStageStatistic(statistics, FieldStage.DECOMMISSIONING).getTotalProjects()).isEqualTo(1);
    assertThat(getFieldStageStatistic(statistics, FieldStage.DISCOVERY).getTotalProjects()).isZero();
    assertThat(getFieldStageStatistic(statistics, FieldStage.ENERGY_TRANSITION).getTotalProjects()).isZero();
  }

  @Test
  public void getQuarterlyStatisticsByFieldStage_assertOnlyProjectsUpdateInQuarterAreIncluded() {

    // Workaround for test to pass until we fix PAT-518
    final var timeInCurrentQuarter = DateUtil.getQuarterFromLocalDate(LocalDate.now()).getEndDateAsInstant().minus(5, ChronoUnit.DAYS);
    final var timeNotInCurrentQuarter = Instant.now().minus(100, ChronoUnit.DAYS);

    final var reportableProjectView1 = ReportableProjectTestUtil.createReportableProjectView(
        FieldStage.DISCOVERY,
        1,
        timeInCurrentQuarter
    );
    final var reportableProjectView2 = ReportableProjectTestUtil.createReportableProjectView(
        FieldStage.DISCOVERY,
        2,
        timeInCurrentQuarter
    );
    final var reportableProjectView3 = ReportableProjectTestUtil.createReportableProjectView(
        FieldStage.DECOMMISSIONING,
        3,
        timeNotInCurrentQuarter
    );
    final var reportableProjectView4 = ReportableProjectTestUtil.createReportableProjectView(
        FieldStage.DECOMMISSIONING,
        4,
        timeInCurrentQuarter
    );

    final var reportableProjectViews = List.of(
        reportableProjectView1,
        reportableProjectView2,
        reportableProjectView3,
        reportableProjectView4
    );

    final var statistics = quarterlyStatisticsService.getQuarterlyStatisticsByFieldStage(
        reportableProjectViews
    );

    final var discoveryStat = getFieldStageStatistic(statistics, FieldStage.DISCOVERY);

    assertThat(discoveryStat.getTotalProjects()).isEqualTo(2);
    assertThat(discoveryStat.getTotalProjectsUpdateThisQuarter()).isEqualTo(2);
    assertThat(discoveryStat.getPercentageOfProjectsUpdated()).isEqualTo(100.0);

    final var decommissioningStat = getFieldStageStatistic(statistics, FieldStage.DECOMMISSIONING);

    assertThat(decommissioningStat.getTotalProjects()).isEqualTo(2);
    assertThat(decommissioningStat.getTotalProjectsUpdateThisQuarter()).isEqualTo(1);
    assertThat(decommissioningStat.getPercentageOfProjectsUpdated()).isEqualTo(50.0);

  }

  @Test
  public void getQuarterlyStatisticsModelAndView() {
    final var modelMap = quarterlyStatisticsService.getQuarterlyStatisticsModelAndView().getModel();
    assertThat(modelMap)
        .containsOnlyKeys("pageTitle", "fieldStageStatistics", "operatorReportableProjects")
        .containsEntry("pageTitle", QuarterlyStatisticsController.QUARTERLY_STATISTICS_TITLE);
  }

  @Test
  public void getReportableProjectsByOperator_whenNoResults_thenEmptyMap() {
    final var result = quarterlyStatisticsService.getReportableProjectsByOperator(Collections.emptyList());
    assertThat(result).isEmpty();
  }

  @Test
  public void getReportableProjectsByOperator_whenResults_thenGroupedByOperatorOrderedByProjectName() {

    final var operatorA = "operator a";
    final var operatorB = "operator b";

    var reportableProjectView1 = ReportableProjectTestUtil.createReportableProjectView(operatorB, "A project");
    var reportableProjectView2 = ReportableProjectTestUtil.createReportableProjectView(operatorA, "B project");
    var reportableProjectView3 = ReportableProjectTestUtil.createReportableProjectView(operatorA, "a project");

    final var reportableProjectViews = List.of(
        reportableProjectView1,
        reportableProjectView2,
        reportableProjectView3
    );

    final var result = quarterlyStatisticsService.getReportableProjectsByOperator(
        reportableProjectViews
    );
    assertThat(result).containsExactly(
        entry(operatorA, List.of(reportableProjectView3, reportableProjectView2)),
        entry(operatorB, List.of(reportableProjectView1))
    );
  }

  private FieldStageStatistic getFieldStageStatistic(
      List<FieldStageStatistic> fieldStageStatistics,
      FieldStage fieldStage
  ) {
    return fieldStageStatistics
        .stream()
        .filter(fieldStageStatistic -> fieldStageStatistic.getFieldStage().equals(fieldStage.getDisplayName()))
        .findFirst()
        .orElse(null);
  }

}