package uk.co.ogauthority.pathfinder.event.contributor;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectcontribution.ProjectContributor;

@Component
public class ContributorsAddedEventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  @Autowired
  public ContributorsAddedEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public void publishContributorsAddedEvent(final List<ProjectContributor> projectContributors,
                                            ProjectDetail projectDetail) {
    var event = new ContributorsAddedEvent(this, projectContributors, projectDetail.getId());
    applicationEventPublisher.publishEvent(event);
  }
}
