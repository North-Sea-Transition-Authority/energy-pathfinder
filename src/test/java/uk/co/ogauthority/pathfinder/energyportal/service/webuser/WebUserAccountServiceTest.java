package uk.co.ogauthority.pathfinder.energyportal.service.webuser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.energyportal.model.WebUserAccountStatus;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.repository.WebUserAccountRepository;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@ExtendWith(MockitoExtension.class)
class WebUserAccountServiceTest {

  private static final int WUA_ID = 4;

  @Mock
  private WebUserAccountRepository webUserAccountRepository;

  private WebUserAccountService webUserAccountService;

  private WebUserAccount webUserAccount;

  @BeforeEach
  void setup() {
    webUserAccountService = new WebUserAccountService(webUserAccountRepository);

    webUserAccount = UserTestingUtil.getWebUserAccount();
  }

  @Test
  void getWebUserAccountOrError_whenExists_thenReturn() {
    when(webUserAccountRepository.findById(WUA_ID)).thenReturn(
        Optional.of(webUserAccount)
    );

    var result = webUserAccountService.getWebUserAccountOrError(WUA_ID);

    assertThat(result).isEqualTo(webUserAccount);
  }

  @Test
  void getWebUserAccountOrError_whenNotFound_thenException() {
    when(webUserAccountRepository.findById(WUA_ID)).thenReturn(
        Optional.empty()
    );

    assertThatThrownBy(() -> webUserAccountService.getWebUserAccountOrError(WUA_ID))
        .isInstanceOf(PathfinderEntityNotFoundException.class);
  }

  @Test
  void getWebUserAccount_whenExists_thenReturn() {
    when(webUserAccountRepository.findById(WUA_ID)).thenReturn(
        Optional.of(webUserAccount)
    );

    var result = webUserAccountService.getWebUserAccount(WUA_ID);

    assertThat(result.get()).isEqualTo(webUserAccount);
  }

  @Test
  void getWebUserAccount_whenNotExists_thenReturnEmpty() {
    when(webUserAccountRepository.findById(WUA_ID)).thenReturn(
        Optional.empty()
    );

    var result = webUserAccountService.getWebUserAccount(WUA_ID);

    assertThat(result).isEmpty();
  }

  @Test
  void getWebUserAccounts_whenFound_thenReturnPopulatedList() {
    final var webUserAccountIds = List.of(webUserAccount.getWuaId());
    when(webUserAccountRepository.findAllByWuaIdIn(webUserAccountIds)).thenReturn(List.of(webUserAccount));
    final var result = webUserAccountService.getWebUserAccounts(webUserAccountIds);
    assertThat(result).containsExactly(webUserAccount);
  }

  @Test
  void getWebUserAccounts_whenNotFound_thenReturnEmptyList() {
    final var webUserAccountIds = List.of(webUserAccount.getWuaId());
    when(webUserAccountRepository.findAllByWuaIdIn(webUserAccountIds)).thenReturn(List.of());
    final var result = webUserAccountService.getWebUserAccounts(webUserAccountIds);
    assertThat(result).isEmpty();
  }

  @Test
  void findByPerson_whenExists_thenReturn() {

    Person person = UserTestingUtil.getPerson();

    when(webUserAccountRepository.findByPerson(person))
        .thenReturn(List.of(webUserAccount));

    var resultingWebUserAccount = webUserAccountService.findByPerson(person);

    assertThat(resultingWebUserAccount).isEqualTo(Optional.of(webUserAccount));
  }

  @Test
  void findByPerson_whenDoesNotExists_thenEmpty() {

    Person person = UserTestingUtil.getPerson();

    when(webUserAccountRepository.findByPerson(person))
        .thenReturn(List.of());

    var resultingWebUserAccount = webUserAccountService.findByPerson(person);

    assertThat(resultingWebUserAccount).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(value = WebUserAccountStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "ACTIVE")
  void findByPerson_whenMultipleAccounts_activeOnly(WebUserAccountStatus nonActiveWebUserAccountStatus) {

    Person person = UserTestingUtil.getPerson();

    var activeWebUserAccount = new WebUserAccount(
        10,
        "title",
        "forename",
        "surname",
        "email-address",
        "loginId",
        WebUserAccountStatus.ACTIVE,
        person
    );

    var cancelledWebUserAccount = new WebUserAccount(
        20,
        "title",
        "forename",
        "surname",
        "email-address",
        "loginId",
        nonActiveWebUserAccountStatus,
        person
    );

    when(webUserAccountRepository.findByPerson(person))
        .thenReturn(List.of(activeWebUserAccount, cancelledWebUserAccount));

    var resultingWebUserAccount = webUserAccountService.findByPerson(person);

    assertThat(resultingWebUserAccount).isEqualTo(Optional.of(activeWebUserAccount));

  }

  @Test
  void findByPerson_whenNoActiveAccounts_activeOnly() {

    Person person = UserTestingUtil.getPerson();

    var suspendedWebUserAccount = new WebUserAccount(
        10,
        "title",
        "forename",
        "surname",
        "email-address",
        "loginId",
        WebUserAccountStatus.SUSPENDED,
        person
    );

    var cancelledWebUserAccount = new WebUserAccount(
        20,
        "title",
        "forename",
        "surname",
        "email-address",
        "loginId",
        WebUserAccountStatus.CANCELLED,
        person
    );

    when(webUserAccountRepository.findByPerson(person))
        .thenReturn(List.of(suspendedWebUserAccount, cancelledWebUserAccount));

    var resultingWebUserAccount = webUserAccountService.findByPerson(person);

    assertThat(resultingWebUserAccount).isEmpty();
  }
}
