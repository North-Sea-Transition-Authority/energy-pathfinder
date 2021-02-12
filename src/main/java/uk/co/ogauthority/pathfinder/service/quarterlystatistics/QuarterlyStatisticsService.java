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
        .addObject("fieldStageStatistics", getQuarterlyStatisticsByFieldStage(reportableProjectViews))
        .addObject("operatorReportableProjects", getReportableProjectsByOperator(reportableProjectViews));
  }

  protected List<FieldStageStatistic> getQuarterlyStatisticsByFieldStage(
      List<ReportableProjectView> reportableProjects
  ) {

    final var reportableProjectViewsByFieldStage = reportableProjects
        .stream()
        .collect(Collectors.groupingBy(ReportableProjectView::getFieldStage));

    List<FieldStageStatistic> fieldStageStatistics = new ArrayList<>();

    Arrays.stream(FieldStage.values()).forEach(fieldStage -> {
      var totalProjectsForFieldStage = 0;
      var totalProjectUpdatedForFieldStage = 0;
      var hasReportableProjects = reportableProjectViewsByFieldStage.containsKey(fieldStage);

      if (hasReportableProjects) {
        var fieldStageReportableProjects = reportableProjectViewsByFieldStage.get(fieldStage);
        totalProjectsForFieldStage = fieldStageReportableProjects.size();
        totalProjectUpdatedForFieldStage = getReportableProjectsForCurrentQuarter(fieldStageReportableProjects).size();
      }

      var quarterlyStatistic = new FieldStageStatistic(
          fieldStage.getDisplayName(),
          totalProjectsForFieldStage,
          totalProjectUpdatedForFieldStage
      );

      fieldStageStatistics.add(quarterlyStatistic);
    });

    return fieldStageStatistics;
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
            .thenComparing(reportableProjectView -> reportableProjectView.getProjectTitle().toLowerCase())
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
