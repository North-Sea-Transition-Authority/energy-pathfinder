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
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.quarterlystatistics.QuarterlyStatisticsController;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
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

    assertAllFieldStageStatisticTotalsAreZero(statistics);
  }

  @Test
  public void getQuarterlyStatisticsByFieldStage_whenNoInfrastructureReportableProjects_allFieldStagesReturnedWithZeroValues() {

    final var nonInfrastructureReportableProject = ReportableProjectTestUtil.createReportableProjectView(ProjectType.FORWARD_WORK_PLAN);

    final List<ReportableProjectView> nonInfrastructureReportableProjects = List.of(nonInfrastructureReportableProject);

    final var statistics = quarterlyStatisticsService.getQuarterlyStatisticsByFieldStage(
        nonInfrastructureReportableProjects
    );

    assertAllFieldStageStatisticTotalsAreZero(statistics);
  }

  @Test
  public void getQuarterlyStatisticsByFieldStage_assertProjectTotalsAreCorrect() {

    final var developmentReportableProjectView1 = ReportableProjectTestUtil.createReportableProjectView(FieldStage.DEVELOPMENT);
    final var developmentReportableProjectView2 = ReportableProjectTestUtil.createReportableProjectView(FieldStage.DEVELOPMENT);
    final var decommissioningReportableProjectView = ReportableProjectTestUtil.createReportableProjectView(FieldStage.DECOMMISSIONING);

    final var reportableProjectViews = List.of(
        developmentReportableProjectView1,
        developmentReportableProjectView2,
        decommissioningReportableProjectView
    );

    final var statistics = quarterlyStatisticsService.getQuarterlyStatisticsByFieldStage(
        reportableProjectViews
    );

    assertThat(getProjectUpdateStatisticByFieldStage(statistics, FieldStage.DEVELOPMENT).getTotalProjects()).isEqualTo(2);
    assertThat(getProjectUpdateStatisticByFieldStage(statistics, FieldStage.DECOMMISSIONING).getTotalProjects()).isEqualTo(1);

    final var fieldStagesWithZeroTotalExpected = Lists.newArrayList(FieldStage.values());
    fieldStagesWithZeroTotalExpected.removeAll(List.of(FieldStage.DECOMMISSIONING, FieldStage.DEVELOPMENT));

    fieldStagesWithZeroTotalExpected.forEach(fieldStage -> {
      final var fieldStageStatistic = getProjectUpdateStatisticByFieldStage(statistics, fieldStage);
      assertThat(fieldStageStatistic.getTotalProjects()).isZero();
    });
  }

  @Test
  public void getQuarterlyStatisticsByFieldStage_assertOnlyProjectsUpdateInQuarterAreIncluded() {

    final var timeInCurrentQuarter = DateUtil.getQuarterFromLocalDate(LocalDate.now()).getEndDateAsInstant();
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

    final var discoveryStat = getProjectUpdateStatisticByFieldStage(statistics, FieldStage.DISCOVERY);

    assertThat(discoveryStat.getTotalProjects()).isEqualTo(2);
    assertThat(discoveryStat.getTotalProjectsUpdated()).isEqualTo(2);
    assertThat(discoveryStat.getPercentageOfProjectsUpdated()).isEqualTo(100.0);

    final var decommissioningStat = getProjectUpdateStatisticByFieldStage(statistics, FieldStage.DECOMMISSIONING);

    assertThat(decommissioningStat.getTotalProjects()).isEqualTo(2);
    assertThat(decommissioningStat.getTotalProjectsUpdated()).isEqualTo(1);
    assertThat(decommissioningStat.getPercentageOfProjectsUpdated()).isEqualTo(50.0);

  }

  @Test
  public void getQuarterlyStatisticsModelAndView() {
    final var modelMap = quarterlyStatisticsService.getQuarterlyStatisticsModelAndView().getModel();
    assertThat(modelMap)
        .containsOnlyKeys("pageTitle", "projectUpdateStatistics", "operatorReportableProjects")
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

  @Test
  public void getReportableProjectsByOperator_assertExpectedProjectTypesReturned() {

    var infrastructureProjectView = ReportableProjectTestUtil.createReportableProjectView(ProjectType.INFRASTRUCTURE);
    var forwardWorkPlanProjectView = ReportableProjectTestUtil.createReportableProjectView(ProjectType.FORWARD_WORK_PLAN);

    final var reportableProjectViews = List.of(
        infrastructureProjectView,
        forwardWorkPlanProjectView
    );

    final var result = quarterlyStatisticsService.getReportableProjectsByOperator(
        reportableProjectViews
    );
    assertThat(result).containsExactly(
        entry(infrastructureProjectView.getOperatorName(), List.of(infrastructureProjectView, forwardWorkPlanProjectView))
    );
  }

  @Test
  public void getForwardWorkPlanQuarterlyUpdateStatistic_whenNoReportableProjects_assertTotalsAreZero() {

    final List<ReportableProjectView> reportableProjects = Collections.emptyList();

    final var resultingProjectUpdateStatistic = quarterlyStatisticsService.getForwardWorkPlanQuarterlyUpdateStatistic(
        reportableProjects
    );

    assertThat(resultingProjectUpdateStatistic.getTotalProjects()).isZero();
    assertThat(resultingProjectUpdateStatistic.getTotalProjectsUpdated()).isZero();
    assertThat(resultingProjectUpdateStatistic.getPercentageOfProjectsUpdated()).isZero();
  }

  @Test
  public void getForwardWorkPlanQuarterlyUpdateStatistic_whenNoForwardWorkPlans_assertTotalsAreZero() {

    final var nonForwardWorkPlanReportableProject = ReportableProjectTestUtil.createReportableProjectView(ProjectType.INFRASTRUCTURE);
    final var reportableProjectsWithNoWorkPlans = List.of(nonForwardWorkPlanReportableProject);

    final var resultingProjectUpdateStatistic = quarterlyStatisticsService.getForwardWorkPlanQuarterlyUpdateStatistic(
        reportableProjectsWithNoWorkPlans
    );

    assertThat(resultingProjectUpdateStatistic.getTotalProjects()).isZero();
    assertThat(resultingProjectUpdateStatistic.getTotalProjectsUpdated()).isZero();
    assertThat(resultingProjectUpdateStatistic.getPercentageOfProjectsUpdated()).isZero();
  }

  @Test
  public void getForwardWorkPlanQuarterlyUpdateStatistic_whenForwardWorkPlans_assertTotalProjectsIsCorrect() {

    final var nonForwardWorkPlanReportableProject = ReportableProjectTestUtil.createReportableProjectView(ProjectType.INFRASTRUCTURE);

    final var forwardWorkPlanReportableProject1 = ReportableProjectTestUtil.createReportableProjectView(ProjectType.FORWARD_WORK_PLAN);
    final var forwardWorkPlanReportableProject2 = ReportableProjectTestUtil.createReportableProjectView(ProjectType.FORWARD_WORK_PLAN);

    final var reportableProjects = List.of(
        nonForwardWorkPlanReportableProject,
        forwardWorkPlanReportableProject1,
        forwardWorkPlanReportableProject2
    );

    final var resultingProjectUpdateStatistic = quarterlyStatisticsService.getForwardWorkPlanQuarterlyUpdateStatistic(
        reportableProjects
    );

    assertThat(resultingProjectUpdateStatistic.getTotalProjects()).isEqualTo(2);
  }

  @Test
  public void getForwardWorkPlanQuarterlyUpdateStatistic_whenForwardWorkPlans_assertOnlyProjectsUpdateInQuarterTotalIsCorrect() {

    final var timeInCurrentQuarter = DateUtil.getQuarterFromLocalDate(LocalDate.now()).getEndDateAsInstant();
    final var timeNotInCurrentQuarter = Instant.now().minus(100, ChronoUnit.DAYS);

    final var updatedForwardWorkPlanReportableProject = ReportableProjectTestUtil.createReportableProjectView(
        ProjectType.FORWARD_WORK_PLAN,
        timeInCurrentQuarter
    );

    final var nonUpdatedForwardWorkPlanReportableProject = ReportableProjectTestUtil.createReportableProjectView(
        ProjectType.FORWARD_WORK_PLAN,
        timeNotInCurrentQuarter
    );

    final var reportableProjects = List.of(
        nonUpdatedForwardWorkPlanReportableProject,
        updatedForwardWorkPlanReportableProject
    );

    final var resultingProjectUpdateStatistic = quarterlyStatisticsService.getForwardWorkPlanQuarterlyUpdateStatistic(
        reportableProjects
    );

    assertThat(resultingProjectUpdateStatistic.getTotalProjects()).isEqualTo(2);
    assertThat(resultingProjectUpdateStatistic.getTotalProjectsUpdated()).isEqualTo(1);
    assertThat(resultingProjectUpdateStatistic.getPercentageOfProjectsUpdated()).isEqualTo(50.0);
  }

  @Test
  public void getForwardWorkPlanQuarterlyUpdateStatistic_assertStatisticDisplayNameIsForwardWorkPlan() {

    final var projectTypeRestriction = ProjectType.FORWARD_WORK_PLAN;

    final var forwardWorkPlanReportableProject = ReportableProjectTestUtil.createReportableProjectView(
        projectTypeRestriction
    );

    final var resultingProjectUpdateStatistic = quarterlyStatisticsService.getForwardWorkPlanQuarterlyUpdateStatistic(
        List.of(forwardWorkPlanReportableProject)
    );

    assertThat(resultingProjectUpdateStatistic.getStatisticPrompt()).isEqualTo(projectTypeRestriction.getDisplayName());
  }

  @Test
  public void getProjectUpdateStatistics_verifyExpectedStatisticOrdering() {

    final var resultingProjectUpdateStatistics = quarterlyStatisticsService.getProjectUpdateStatistics(
        Collections.emptyList()
    );

    final var resultingStatisticPrompts = resultingProjectUpdateStatistics
        .stream()
        .map(ProjectUpdateStatistic::getStatisticPrompt)
        .collect(Collectors.toList());

    final var expectedStatisticPrompts = Arrays.stream(FieldStage.values())
        .map(FieldStage::getDisplayName)
        .collect(Collectors.toList());

    expectedStatisticPrompts.add(ProjectType.FORWARD_WORK_PLAN.getDisplayName());

    assertThat(resultingStatisticPrompts).isEqualTo(expectedStatisticPrompts);
  }

  private ProjectUpdateStatistic getProjectUpdateStatisticByFieldStage(
      List<ProjectUpdateStatistic> projectUpdateStatistics,
      FieldStage fieldStage
  ) {
    return projectUpdateStatistics
        .stream()
        .filter(projectUpdateStatistic -> projectUpdateStatistic.getStatisticPrompt().equals(fieldStage.getDisplayName()))
        .findFirst()
        .orElse(null);
  }

  private void assertAllFieldStageStatisticTotalsAreZero(List<ProjectUpdateStatistic> fieldStageUpdateStatistics) {

    Arrays.asList(FieldStage.values()).forEach(fieldStage -> {

      final var fieldStageStatisticOptional = fieldStageUpdateStatistics
          .stream()
          .filter(projectUpdateStatistic -> projectUpdateStatistic.getStatisticPrompt().equals(fieldStage.getDisplayName()))
          .findFirst();

      assertThat(fieldStageStatisticOptional).isPresent();

      final var fieldStageStatistic = fieldStageStatisticOptional.get();

      assertThat(fieldStageStatistic.getTotalProjects()).isZero();
      assertThat(fieldStageStatistic.getTotalProjectsUpdated()).isZero();
      assertThat(fieldStageStatistic.getPercentageOfProjectsUpdated()).isZero();

    });
  }

}