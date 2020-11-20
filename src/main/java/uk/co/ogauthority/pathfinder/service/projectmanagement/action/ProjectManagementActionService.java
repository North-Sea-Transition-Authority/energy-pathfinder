package uk.co.ogauthority.pathfinder.service.projectmanagement.action;

import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.form.useraction.UserAction;

@Service
public class ProjectManagementActionService {

  public List<UserAction> getUserActions(AuthenticatedUserAccount user) {
    return Collections.emptyList();
  }
}
