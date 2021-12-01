package uk.co.ogauthority.pathfinder.service.quarterlystatistics;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.quarterlystatistics.ReportableProject;
import uk.co.ogauthority.pathfinder.repository.quarterlystatistics.ReportableProjectRepository;

@Service
public class ReportableProjectService {

  private final ReportableProjectRepository reportableProjectRepository;

  @Autowired
  public ReportableProjectService(ReportableProjectRepository reportableProjectRepository) {
    this.reportableProjectRepository = reportableProjectRepository;
  }

  public List<ReportableProjectView> getReportableProjectViews() {
    return getReportableProjects()
        .stream()
        .map(this::convertToReportableProjectView)
        .collect(Collectors.toList());
  }

  public List<ReportableProject> getReportableProjectsUpdatedBetween(Instant earliestUpdatedDatetime,
                                                                     Instant latestUpdatedDatetime) {
    return reportableProjectRepository.findByLastUpdatedDatetimeBetween(earliestUpdatedDatetime, latestUpdatedDatetime);
  }

  public List<ReportableProject> getReportableProjectsNotUpdatedBetween(Instant earliestUpdatedDatetime,
                                                                        Instant latestUpdatedDatetime) {
    return reportableProjectRepository.findByLastUpdatedDatetimeNotBetween(earliestUpdatedDatetime, latestUpdatedDatetime);
  }

  public List<ReportableProject> getReportableProjects() {
    return reportableProjectRepository.findAll();
  }

  private ReportableProjectView convertToReportableProjectView(ReportableProject reportableProject) {
    return new ReportableProjectView(reportableProject);
  }
}
