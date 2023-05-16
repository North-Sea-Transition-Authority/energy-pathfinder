package uk.co.ogauthority.pathfinder.event.contributor;

import java.util.List;
import org.springframework.context.ApplicationEvent;
import uk.co.ogauthority.pathfinder.model.entity.project.projectcontribution.ProjectContributor;

public class ContributorsAddedEvent extends ApplicationEvent {

  private final List<ProjectContributor> projectContributors;
  private final Integer projectDetailId;

  public ContributorsAddedEvent(Object source,
                                List<ProjectContributor> projectContributors,
                                Integer projectDetailId) {
    super(source);
    this.projectContributors = projectContributors;
    this.projectDetailId = projectDetailId;
  }

  public List<ProjectContributor> getProjectContributors() {
    return projectContributors;
  }

  public Integer getProjectDetailId() {
    return projectDetailId;
  }
}
