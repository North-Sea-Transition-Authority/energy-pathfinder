package uk.co.ogauthority.pathfinder.energyportal.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import uk.co.ogauthority.pathfinder.energyportal.model.WebUserAccountStatus;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;

public interface WebUserAccountRepository extends CrudRepository<WebUserAccount, Integer> {

  List<WebUserAccount> findAllByEmailAddressIgnoreCaseAndAccountStatusNot(String emailAddress, WebUserAccountStatus accountStatus);

  List<WebUserAccount> findAllByWuaIdIn(List<Integer> webUserAccounts);

  List<WebUserAccount> findByPerson(Person person);
}
