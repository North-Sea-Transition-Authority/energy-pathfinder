package uk.co.ogauthority.pathfinder.event.contributor;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectcontribution.ProjectContributor;

@Component
public class ContributorsDeletedEventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  @Autowired
  public ContributorsDeletedEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public void publishContributorsDeletedEvent(final List<ProjectContributor> projectContributors,
                                              ProjectDetail projectDetail) {
    var event = new ContributorsDeletedEvent(this, projectContributors, projectDetail);
    applicationEventPublisher.publishEvent(event);
  }
}
