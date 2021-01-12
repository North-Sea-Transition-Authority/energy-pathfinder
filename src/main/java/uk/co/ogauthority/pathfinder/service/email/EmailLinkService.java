package uk.co.ogauthority.pathfinder.service.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@Service
public class EmailLinkService {

  @Value("${pathfinder.url.base}")
  private String pathfinderUrlBase;

  @Value("${context-path}")
  private String contextPath;

  public String generateProjectManagementUrl(Project project) {
    return pathfinderUrlBase + contextPath + ControllerUtils.getProjectManagementUrl(project.getId());
  }

  public String getWorkAreaUrl() {
    return pathfinderUrlBase + contextPath + ControllerUtils.getWorkAreaUrl();
  }

}
