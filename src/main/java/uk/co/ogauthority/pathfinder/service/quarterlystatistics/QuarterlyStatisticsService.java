package uk.co.ogauthority.pathfinder.service.quarterlystatistics;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.quarterlystatistics.QuarterlyStatisticsController;
import uk.co.ogauthority.pathfinder.model.entity.quarterlystatistics.ReportableProject;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.repository.quarterlystatistics.ReportableProjectRepository;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@Service
public class QuarterlyStatisticsService {

  private final ReportableProjectRepository reportableProjectRepository;

  @Autowired
  public QuarterlyStatisticsService(ReportableProjectRepository reportableProjectRepository) {
    this.reportableProjectRepository = reportableProjectRepository;
  }

  public ModelAndView getQuarterlyStatisticsModelAndView() {
    return new ModelAndView("quarterlystatistics/quarterlyStatistics")
        .addObject("pageTitle", QuarterlyStatisticsController.QUARTERLY_STATISTICS_TITLE)
        .addObject("quarterlyStatistics", getQuarterlyStatisticsByFieldStage());
  }

  public List<FieldStageQuarterlyStatistic> getQuarterlyStatisticsByFieldStage() {

    final var reportableProjectsByFieldStage = getReportableProjects()
        .stream()
        .collect(Collectors.groupingBy(ReportableProject::getFieldStage));

    List<FieldStageQuarterlyStatistic> fieldStageQuarterlyStatistics = new ArrayList<>();

    Arrays.stream(FieldStage.values()).forEach(fieldStage -> {
      var totalProjectsForFieldStage = 0;
      var totalProjectUpdatedForFieldStage = 0;
      var hasReportableProjects = reportableProjectsByFieldStage.containsKey(fieldStage);

      if (hasReportableProjects) {
        var reportableProjects = reportableProjectsByFieldStage.get(fieldStage);
        totalProjectsForFieldStage = reportableProjects.size();
        totalProjectUpdatedForFieldStage = getReportableProjectsForCurrentQuarter(reportableProjects).size();
      }

      var quarterlyStatistic = new FieldStageQuarterlyStatistic(
          fieldStage.getDisplayName(),
          totalProjectsForFieldStage,
          totalProjectUpdatedForFieldStage
      );

      fieldStageQuarterlyStatistics.add(quarterlyStatistic);
    });

    return fieldStageQuarterlyStatistics;
  }

  private List<ReportableProject> getReportableProjects() {
    return reportableProjectRepository.findAll();
  }

  private List<ReportableProject> getReportableProjectsForCurrentQuarter(List<ReportableProject> reportableProjects) {
    final var currentQuarter = DateUtil.getQuarterFromLocalDate(LocalDate.now());
    return reportableProjects
        .stream()
        .filter(reportableProject -> {
          final var projectUpdatedDatetime = reportableProject.getLastUpdatedDatetime();
          final var quarterStartInstant = currentQuarter.getStartDateAsInstant();
          final var quarterEndInstant = currentQuarter.getEndDateAsInstant();
          return DateUtil.isOnOrAfter(projectUpdatedDatetime, quarterStartInstant)
                  && DateUtil.isOnOrBefore(projectUpdatedDatetime, quarterEndInstant);
        })
        .collect(Collectors.toList());
  }
}
