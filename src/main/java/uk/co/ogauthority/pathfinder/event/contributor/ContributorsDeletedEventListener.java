package uk.co.ogauthority.pathfinder.event.contributor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ContributorsDeletedEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContributorsDeletedEventListener.class);

  @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
  public void handleUnsuccessfulDeletion(ContributorsDeletedEvent event) {
    LOGGER.error(
        "Failed to delete {} project contributor(s) from ProjectDetail ID: {}",
        event.getProjectContributors().size(),
        event.getProjectDetailId()
    );
  }
}
