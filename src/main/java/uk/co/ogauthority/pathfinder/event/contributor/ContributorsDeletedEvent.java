package uk.co.ogauthority.pathfinder.event.contributor;

import java.util.List;
import org.springframework.context.ApplicationEvent;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectcontribution.ProjectContributor;

public class ContributorsDeletedEvent  extends ApplicationEvent {

  private final List<ProjectContributor> projectContributors;
  private final ProjectDetail projectDetail;

  public ContributorsDeletedEvent(Object source,
                                  List<ProjectContributor> projectContributors,
                                  ProjectDetail projectDetail) {
    super(source);
    this.projectContributors = projectContributors;
    this.projectDetail = projectDetail;
  }

  public List<ProjectContributor> getProjectContributors() {
    return projectContributors;
  }

  public ProjectDetail getProjectDetail() {
    return projectDetail;
  }
}
