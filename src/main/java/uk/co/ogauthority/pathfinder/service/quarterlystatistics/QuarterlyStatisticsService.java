package uk.co.ogauthority.pathfinder.service.quarterlystatistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.quarterlystatistics.QuarterlyStatisticsController;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@Service
public class QuarterlyStatisticsService {

  private final ReportableProjectService reportableProjectService;

  @Autowired
  public QuarterlyStatisticsService(ReportableProjectService reportableProjectService) {
    this.reportableProjectService = reportableProjectService;
  }

  public ModelAndView getQuarterlyStatisticsModelAndView() {

    final var reportableProjectViews = reportableProjectService.getReportableProjectViews();

    return new ModelAndView("quarterlystatistics/quarterlyStatistics")
        .addObject("pageTitle", QuarterlyStatisticsController.QUARTERLY_STATISTICS_TITLE)
        .addObject("projectUpdateStatistics", getProjectUpdateStatistics(reportableProjectViews))
        .addObject("operatorReportableProjects", getReportableProjectsByOperator(reportableProjectViews));
  }

  protected List<ProjectUpdateStatistic> getProjectUpdateStatistics(List<ReportableProjectView> reportableProjectViews) {
    final var projectUpdateStatistics = getQuarterlyStatisticsByFieldStage(reportableProjectViews);
    projectUpdateStatistics.add(getForwardWorkPlanQuarterlyUpdateStatistic(reportableProjectViews));
    return projectUpdateStatistics;
  }

  protected List<ProjectUpdateStatistic> getQuarterlyStatisticsByFieldStage(
      List<ReportableProjectView> reportableProjects
  ) {

    final var projectTypeRestriction = ProjectType.INFRASTRUCTURE;

    final var reportableProjectViewsByFieldStage = reportableProjects
        .stream()
        .filter(reportableProjectView -> projectTypeRestriction.equals(reportableProjectView.getProjectType()))
        .collect(Collectors.groupingBy(ReportableProjectView::getFieldStage));

    List<ProjectUpdateStatistic> projectUpdateStatistics = new ArrayList<>();

    Arrays.stream(FieldStage.values()).forEach(fieldStage -> {
      var totalProjectsForFieldStage = 0;
      var totalProjectUpdatedForFieldStage = 0;
      var hasReportableProjects = reportableProjectViewsByFieldStage.containsKey(fieldStage);

      if (hasReportableProjects) {
        var fieldStageReportableProjects = reportableProjectViewsByFieldStage.get(fieldStage);
        totalProjectsForFieldStage = fieldStageReportableProjects.size();
        totalProjectUpdatedForFieldStage = getReportableProjectsForCurrentQuarter(fieldStageReportableProjects).size();
      }

      var projectUpdateStatistic = new ProjectUpdateStatistic(
          fieldStage.getDisplayName(),
          totalProjectsForFieldStage,
          totalProjectUpdatedForFieldStage
      );

      projectUpdateStatistics.add(projectUpdateStatistic);
    });

    return projectUpdateStatistics;
  }

  protected ProjectUpdateStatistic getForwardWorkPlanQuarterlyUpdateStatistic(List<ReportableProjectView> reportableProjects) {

    final var projectTypeRestriction = ProjectType.FORWARD_WORK_PLAN;

    final var forwardWorkPlanReportableProjectViews = reportableProjects
        .stream()
        .filter(reportableProjectView -> projectTypeRestriction.equals(reportableProjectView.getProjectType()))
        .collect(Collectors.toList());

    final var publishedForwardWorkPlansCount = forwardWorkPlanReportableProjectViews.size();
    final var forwardWorkPlansUpdated = getReportableProjectsForCurrentQuarter(forwardWorkPlanReportableProjectViews).size();

    return new ProjectUpdateStatistic(
        projectTypeRestriction.getDisplayName(),
        publishedForwardWorkPlansCount,
        forwardWorkPlansUpdated
    );
  }

  /**
   * Get a map of operator name to list of published projects belonging to that operator.
   * @param reportableProjectViews a list of published project
   * @return a map of operator name to list of published projects belonging to that operator
   */
  protected Map<String, List<ReportableProjectView>> getReportableProjectsByOperator(
      List<ReportableProjectView> reportableProjectViews
  ) {
    return reportableProjectViews
        .stream()
        .sorted(Comparator.comparing(ReportableProjectView::getOperatorName)
            .thenComparing(reportableProjectView -> reportableProjectView.getProjectDisplayName().toLowerCase())
        )
        .collect(Collectors.groupingBy(ReportableProjectView::getOperatorName, LinkedHashMap::new, Collectors.toList()));
  }

  private List<ReportableProjectView> getReportableProjectsForCurrentQuarter(
      List<ReportableProjectView> reportableProjectViews
  ) {
    return reportableProjectViews
        .stream()
        .filter(reportableProjectView -> DateUtil.isInCurrentQuarter(reportableProjectView.getLastUpdatedDatetime()))
        .collect(Collectors.toList());
  }
}
