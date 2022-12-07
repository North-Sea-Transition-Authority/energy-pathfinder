package uk.co.ogauthority.pathfinder.event.contributor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import uk.co.ogauthority.pathfinder.service.email.ProjectContributorMailService;

@Component
public class ContributorsDeletedEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContributorsDeletedEventListener.class);

  private final ProjectContributorMailService projectContributorMailService;


  @Autowired
  public ContributorsDeletedEventListener(
      ProjectContributorMailService projectContributorMailService) {
    this.projectContributorMailService = projectContributorMailService;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleSuccessfulDeletion(ContributorsDeletedEvent event) {
    projectContributorMailService.sendContributorsRemovedEmail(
        event.getProjectContributors(),
        event.getProjectDetail()
    );
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
  public void handleUnsuccessfulDeletion(ContributorsDeletedEvent event) {
    LOGGER.error(
        "Failed to delete {} project contributor(s) from ProjectDetail ID: {}",
        event.getProjectContributors().size(),
        event.getProjectDetail().getId()
    );
  }
}
