package uk.co.ogauthority.pathfinder.service.quarterlystatistics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
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
import uk.co.ogauthority.pathfinder.model.entity.quarterlystatistics.ReportableProject;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.repository.quarterlystatistics.ReportableProjectRepository;

@RunWith(MockitoJUnitRunner.class)
public class QuarterlyStatisticsServiceTest {

  @Mock
  private ReportableProjectRepository reportableProjectRepository;

  private QuarterlyStatisticsService quarterlyStatisticsService;

  @Before
  public void setup() {
    quarterlyStatisticsService = new QuarterlyStatisticsService(reportableProjectRepository);
  }

  @Test
  public void getQuarterlyStatisticsByFieldStage_whenNoReportableProjects_allFieldStagesReturnedWithZeroValues() {

    when(reportableProjectRepository.findAll()).thenReturn(Collections.emptyList());
    final var statistics = quarterlyStatisticsService.getQuarterlyStatisticsByFieldStage();

    final var fieldStagesFromStatistics = statistics
        .stream()
        .map(FieldStageQuarterlyStatistic::getFieldStage)
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

    final var reportableProject1 = createReportableProject(FieldStage.DEVELOPMENT);
    final var reportableProject2 = createReportableProject(FieldStage.DEVELOPMENT);
    final var reportableProject3 = createReportableProject(FieldStage.DECOMMISSIONING);

    when(reportableProjectRepository.findAll()).thenReturn(List.of(
        reportableProject1,
        reportableProject2,
        reportableProject3
    ));
    final var statistics = quarterlyStatisticsService.getQuarterlyStatisticsByFieldStage();

    assertThat(getFieldStageQuarterlyStatistic(statistics, FieldStage.DEVELOPMENT).getTotalProjects()).isEqualTo(2);
    assertThat(getFieldStageQuarterlyStatistic(statistics, FieldStage.DECOMMISSIONING).getTotalProjects()).isEqualTo(1);
    assertThat(getFieldStageQuarterlyStatistic(statistics, FieldStage.DISCOVERY).getTotalProjects()).isZero();
    assertThat(getFieldStageQuarterlyStatistic(statistics, FieldStage.OPERATIONS).getTotalProjects()).isZero();
    assertThat(getFieldStageQuarterlyStatistic(statistics, FieldStage.ENERGY_TRANSITION).getTotalProjects()).isZero();
  }

  @Test
  public void getQuarterlyStatisticsByFieldStage_assertOnlyProjectsUpdateInQuarterAreIncluded() {

    final var timeInCurrentQuarter = Instant.now();
    final var timeNotInCurrentQuarter = Instant.now().minus(100, ChronoUnit.DAYS);

    final var reportableProject1 = createReportableProject(FieldStage.DISCOVERY, 1, timeInCurrentQuarter);
    final var reportableProject2 = createReportableProject(FieldStage.DISCOVERY, 2, timeInCurrentQuarter);
    final var reportableProject3 = createReportableProject(FieldStage.DECOMMISSIONING, 3, timeNotInCurrentQuarter);
    final var reportableProject4 = createReportableProject(FieldStage.DECOMMISSIONING, 4, timeInCurrentQuarter);

    when(reportableProjectRepository.findAll()).thenReturn(List.of(
        reportableProject1,
        reportableProject2,
        reportableProject3,
        reportableProject4
    ));
    final var statistics = quarterlyStatisticsService.getQuarterlyStatisticsByFieldStage();

    final var discoveryStat = getFieldStageQuarterlyStatistic(statistics, FieldStage.DISCOVERY);

    assertThat(discoveryStat.getTotalProjects()).isEqualTo(2);
    assertThat(discoveryStat.getTotalProjectsUpdateThisQuarter()).isEqualTo(2);
    assertThat(discoveryStat.getPercentageOfProjectsUpdated()).isEqualTo(100.0);

    final var decommissioningStat = getFieldStageQuarterlyStatistic(statistics, FieldStage.DECOMMISSIONING);

    assertThat(decommissioningStat.getTotalProjects()).isEqualTo(2);
    assertThat(decommissioningStat.getTotalProjectsUpdateThisQuarter()).isEqualTo(1);
    assertThat(decommissioningStat.getPercentageOfProjectsUpdated()).isEqualTo(50.0);

  }

  @Test
  public void getQuarterlyStatisticsModelAndView() {
    final var modelMap = quarterlyStatisticsService.getQuarterlyStatisticsModelAndView().getModel();
    assertThat(modelMap).containsOnlyKeys("pageTitle", "quarterlyStatistics")
        .containsEntry("pageTitle", QuarterlyStatisticsController.QUARTERLY_STATISTICS_TITLE);

    // check list contains an item for each field stage
    assertThat((List<FieldStageQuarterlyStatistic>)modelMap.get("quarterlyStatistics")).hasSize(FieldStage.values().length);
  }

  private ReportableProject createReportableProject(FieldStage fieldStage) {
    return createReportableProject(fieldStage, 1, Instant.now());
  }

  private ReportableProject createReportableProject(FieldStage fieldStage,
                                                    int projectDetailId,
                                                    Instant lastUpdateDatetime) {
    var reportableProject = new ReportableProject();
    reportableProject.setFieldStage(fieldStage);
    reportableProject.setProjectDetailId(projectDetailId);
    reportableProject.setLastUpdatedDatetime(lastUpdateDatetime);
    return reportableProject;
  }

  private FieldStageQuarterlyStatistic getFieldStageQuarterlyStatistic(
      List<FieldStageQuarterlyStatistic> fieldStageQuarterlyStatistics,
      FieldStage fieldStage
  ) {
    return fieldStageQuarterlyStatistics
        .stream()
        .filter(fieldStageQuarterlyStatistic -> fieldStageQuarterlyStatistic.getFieldStage().equals(fieldStage.getDisplayName()))
        .findFirst()
        .orElse(null);
  }

}