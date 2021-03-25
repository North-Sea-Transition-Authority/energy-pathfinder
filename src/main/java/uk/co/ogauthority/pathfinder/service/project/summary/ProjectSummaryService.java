package uk.co.ogauthority.pathfinder.service.project.summary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;

@Service
public class ProjectSummaryService {

  private final List<? extends ProjectSectionSummaryService> sectionSummaryServices;

  @Autowired
  public ProjectSummaryService(List<? extends ProjectSectionSummaryService> sectionSummaryServices) {
    this.sectionSummaryServices = sectionSummaryServices;
  }

  /**
   * Gets an ordered list of each summary section.
   * @param detail project detail to summarise
   * @return list of ProjectSectionSummary objects appropriate for the detail provided
   */
  public List<ProjectSectionSummary> summarise(ProjectDetail detail) {
    var sectionSummaries = new ArrayList<ProjectSectionSummary>();
    sectionSummaryServices.stream()
        .filter(sectionSummaryService -> sectionSummaryService.canShowSection(detail))
        .forEach(sectionSummaryService -> sectionSummaries.add(sectionSummaryService.getSummary(detail)));

    sectionSummaries.sort(Comparator.comparing(ProjectSectionSummary::getDisplayOrder));

    return sectionSummaries;
  }


}
