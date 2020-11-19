package uk.co.ogauthority.pathfinder.service.projectmanagement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementSection;

@Service
public class ProjectManagementService {

  private final List<? extends ProjectManagementSectionService> sectionServices;

  @Autowired
  public ProjectManagementService(List<? extends ProjectManagementSectionService> sectionServices) {
    this.sectionServices = sectionServices;
  }

  public List<ProjectManagementSection> getSections(ProjectDetail projectDetail,
                                                    AuthenticatedUserAccount user) {
    var sections = new ArrayList<ProjectManagementSection>();
    sectionServices.forEach(
        sectionService -> sections.add(sectionService.getSection(projectDetail, user))
    );

    sections.sort(Comparator.comparing(ProjectManagementSection::getDisplayOrder));

    return sections;
  }
}
