package uk.co.ogauthority.pathfinder.event.contributor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.service.email.ProjectContributorMailService;

@Component
public class ContributorsDeletedEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContributorsDeletedEventListener.class);

  private final ProjectContributorMailService projectContributorMailService;
  private final ProjectDetailsRepository projectDetailsRepository;


  @Autowired
  public ContributorsDeletedEventListener(
      ProjectContributorMailService projectContributorMailService,
      ProjectDetailsRepository projectDetailsRepository) {
    this.projectContributorMailService = projectContributorMailService;
    this.projectDetailsRepository = projectDetailsRepository;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleSuccessfulDeletion(ContributorsDeletedEvent event) {
    var projectDetail = projectDetailsRepository.findById(event.getProjectDetailId())
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format("Could not find ProjectDetail with ID [%d]", event.getProjectDetailId())));
    projectContributorMailService.sendContributorsRemovedEmail(
        event.getProjectContributors(),
        projectDetail
    );
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
  public void handleUnsuccessfulDeletion(ContributorsDeletedEvent event) {
    LOGGER.error(
        "Failed to delete {} project contributor(s) from ProjectDetail ID: {}",
        event.getProjectContributors().size(),
        event.getProjectDetailId()
    );
  }
}
