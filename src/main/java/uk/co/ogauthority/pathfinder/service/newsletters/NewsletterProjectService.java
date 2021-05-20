package uk.co.ogauthority.pathfinder.service.newsletters;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.quarterlystatistics.ReportableProject;
import uk.co.ogauthority.pathfinder.service.quarterlystatistics.ReportableProjectService;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@Service
public class NewsletterProjectService {

  private final ReportableProjectService reportableProjectService;

  public NewsletterProjectService(ReportableProjectService reportableProjectService) {
    this.reportableProjectService = reportableProjectService;
  }

  protected List<String> getProjectsUpdatedInTheLastMonth() {
    return getReportableProjectsUpdatedInTheLastMonth()
        .stream()
        .map(this::convertReportableProjectToEmailRepresentation)
        .sorted()
        .collect(Collectors.toList());
  }

  private List<ReportableProject> getReportableProjectsUpdatedInTheLastMonth() {

    final var dateOneMonthPriorToToday = LocalDate.now().minusMonths(1);

    return reportableProjectService.getReportableProjectsUpdatedBetween(
        DateUtil.getStartOfMonth(dateOneMonthPriorToToday),
        DateUtil.getEndOfMonth(dateOneMonthPriorToToday)
    );
  }

  private String convertReportableProjectToEmailRepresentation(ReportableProject reportableProject) {
    return String.format(
        "%s - %s",
        reportableProject.getOperatorName(),
        reportableProject.getProjectTitle()
    );
  }
}
