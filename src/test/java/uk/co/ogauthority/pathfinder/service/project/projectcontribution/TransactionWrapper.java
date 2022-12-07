package uk.co.ogauthority.pathfinder.service.project.projectcontribution;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
/*
 * This is used to ensure a new transaction is used and therefore can be committed/rolled-back independently
 */
public class TransactionWrapper {
  public void runInNewTransaction(Runnable toRun) {
    toRun.run();
  }
}
