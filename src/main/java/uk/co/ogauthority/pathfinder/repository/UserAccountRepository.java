package uk.co.ogauthority.pathfinder.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import uk.co.ogauthority.pathfinder.model.entity.UserAccount;
import uk.co.ogauthority.pathfinder.model.entity.UserAccountView;

public interface UserAccountRepository extends CrudRepository<UserAccount, String> {

  List<UserAccountView> findByIdIn(List<String> ids);

}
