package uk.co.ogauthority.pathfinder.event.contributor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.service.email.ProjectContributorMailService;

@Component
public class ContributorsAddedEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContributorsAddedEventListener.class);

  private final ProjectContributorMailService projectContributorMailService;
  private final ProjectDetailsRepository projectDetailsRepository;

  @Autowired
  public ContributorsAddedEventListener(
      ProjectContributorMailService projectContributorMailService,
      ProjectDetailsRepository projectDetailsRepository) {
    this.projectContributorMailService = projectContributorMailService;
    this.projectDetailsRepository = projectDetailsRepository;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleSuccessfulAddition(ContributorsAddedEvent event) {
    var projectDetail = projectDetailsRepository.findById(event.getProjectDetailId())
        .orElseThrow(() -> new IllegalStateException(
            String.format("Could not find ProjectDetail with ID [%d]", event.getProjectDetailId())));
    projectContributorMailService.sendContributorsAddedEmail(
        event.getProjectContributors(),
        projectDetail
    );
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
  public void handleUnsuccessfulAddition(ContributorsAddedEvent event) {
    LOGGER.error(
        "Failed to add {} project contributor(s) to ProjectDetail ID: {}",
        event.getProjectContributors().size(),
        event.getProjectDetailId()
    );
  }
}
