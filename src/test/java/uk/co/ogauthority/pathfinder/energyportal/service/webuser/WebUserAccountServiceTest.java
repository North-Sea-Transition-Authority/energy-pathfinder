package uk.co.ogauthority.pathfinder.energyportal.service.webuser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.repository.WebUserAccountRepository;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class WebUserAccountServiceTest {

  private static final int WUA_ID = 4;

  @Mock
  private WebUserAccountRepository webUserAccountRepository;

  private WebUserAccountService webUserAccountService;

  private WebUserAccount webUserAccount;

  @Before
  public void setup() {
    webUserAccountService = new WebUserAccountService(webUserAccountRepository);

    webUserAccount = UserTestingUtil.getWebUserAccount();
  }

  @Test
  public void getWebUserAccountOrError_whenExists_thenReturn() {
    when(webUserAccountRepository.findById(WUA_ID)).thenReturn(
        Optional.of(webUserAccount)
    );

    var result = webUserAccountService.getWebUserAccountOrError(WUA_ID);

    assertThat(result).isEqualTo(webUserAccount);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getWebUserAccountOrError_whenNotFound_thenException() {
    when(webUserAccountRepository.findById(WUA_ID)).thenReturn(
        Optional.empty()
    );

    webUserAccountService.getWebUserAccountOrError(WUA_ID);
  }

  @Test
  public void getWebUserAccounts_whenFound_thenReturnPopulatedList() {
    final var webUserAccountIds = List.of(webUserAccount.getWuaId());
    when(webUserAccountRepository.findAllByWuaIdIn(webUserAccountIds)).thenReturn(List.of(webUserAccount));
    final var result = webUserAccountService.getWebUserAccounts(webUserAccountIds);
    assertThat(result).containsExactly(webUserAccount);
  }

  @Test
  public void getWebUserAccounts_whenNotFound_thenReturnEmptyList() {
    final var webUserAccountIds = List.of(webUserAccount.getWuaId());
    when(webUserAccountRepository.findAllByWuaIdIn(webUserAccountIds)).thenReturn(List.of());
    final var result = webUserAccountService.getWebUserAccounts(webUserAccountIds);
    assertThat(result).isEmpty();
  }
}
