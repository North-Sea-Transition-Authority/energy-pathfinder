package uk.co.ogauthority.pathfinder.energyportal.service.webuser;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.repository.WebUserAccountRepository;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;

@Service
public class WebUserAccountService {

  private final WebUserAccountRepository webUserAccountRepository;

  @Autowired
  public WebUserAccountService(WebUserAccountRepository webUserAccountRepository) {
    this.webUserAccountRepository = webUserAccountRepository;
  }

  public WebUserAccount getWebUserAccountOrError(Integer wuaId) {
    return getWebUserAccount(wuaId)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(String.format("Unable to find WebUserAccount with ID %s", wuaId)));
  }

  public List<WebUserAccount> getWebUserAccounts(List<Integer> webUserAccountIds) {
    return webUserAccountRepository.findAllByWuaIdIn(webUserAccountIds);
  }

  public Optional<WebUserAccount> getWebUserAccount(Integer wuaId) {
    return webUserAccountRepository.findById(wuaId);
  }
}
