package uk.co.ogauthority.pathfinder.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.repository.WebUserAccountRepository;
import uk.co.ogauthority.pathfinder.model.entity.UserSession;
import uk.co.ogauthority.pathfinder.repository.UserSessionRepository;
import uk.co.ogauthority.pathfinder.service.team.TeamService;

@RunWith(MockitoJUnitRunner.class)
public class UserSessionServiceTest {

  private UserSessionService userSessionService;

  private UserSession validSession;
  private UserSession expiredSession;
  private UserSession loggedOutSession;

  @Before
  public void setup() {
    Clock fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault());

    UserSessionRepository userSessionRepository = mock(UserSessionRepository.class);
    WebUserAccountRepository webUserAccountRepository = mock(WebUserAccountRepository.class);
    TeamService teamService = mock(TeamService.class);

    userSessionService = new UserSessionService(userSessionRepository, webUserAccountRepository, teamService, Duration.ofHours(1), fixedClock);

    Instant oneMinuteAgo = fixedClock.instant().minus(Duration.ofMinutes(1));
    Instant oneHourAgo = fixedClock.instant().minus(Duration.ofHours(1));

    validSession = createUserSession("VALID", oneMinuteAgo);
    expiredSession = createUserSession("EXPIRED", oneHourAgo);
    loggedOutSession = createUserSession("LOGGED OUT", oneMinuteAgo);
    loggedOutSession.setLogoutTimestamp(oneMinuteAgo);

    when(userSessionRepository.findById(validSession.getId())).thenReturn(Optional.of(validSession));
    when(userSessionRepository.findById(expiredSession.getId())).thenReturn(Optional.of(expiredSession));
    when(userSessionRepository.findById(loggedOutSession.getId())).thenReturn(Optional.of(loggedOutSession));

    when(webUserAccountRepository.findById(validSession.getWuaId())).thenReturn(Optional.of(validSession.getAuthenticatedUserAccount()));
    when(webUserAccountRepository.findById(expiredSession.getWuaId())).thenReturn(Optional.of(expiredSession.getAuthenticatedUserAccount()));
    when(webUserAccountRepository.findById(loggedOutSession.getWuaId())).thenReturn(Optional.of(loggedOutSession.getAuthenticatedUserAccount()));
  }

  @Test
  public void testGetAndValidateSession_withUserAccountLoad() {
    Optional<UserSession> optionalValidSession = userSessionService.getAndValidateSession(validSession.getId(), true);
    assertThat(optionalValidSession).isPresent();
    assertThat(optionalValidSession.map(UserSession::getId).get()).contains(validSession.getId());

    Optional<UserSession> optionalExpiredSession = userSessionService.getAndValidateSession(expiredSession.getId(), true);
    assertThat(optionalExpiredSession).isEmpty();

    Optional<UserSession> optionalLoggedOutSession = userSessionService.getAndValidateSession(loggedOutSession.getId(), true);
    assertThat(optionalLoggedOutSession).isEmpty();

    Optional<UserSession> missingSession = userSessionService.getAndValidateSession("INVALID", true);
    assertThat(missingSession).isEmpty();
  }

  @Test
  public void testGetAndValidateSession_noUserAccountLoad() {
    Optional<UserSession> optionalValidSession = userSessionService.getAndValidateSession(validSession.getId(), false);
    assertThat(optionalValidSession).isPresent();
    assertThat(optionalValidSession.map(UserSession::getId).get()).contains(validSession.getId());

    Optional<UserSession> optionalExpiredSession = userSessionService.getAndValidateSession(expiredSession.getId(), false);
    assertThat(optionalExpiredSession).isEmpty();

    Optional<UserSession> optionalLoggedOutSession = userSessionService.getAndValidateSession(loggedOutSession.getId(), false);
    assertThat(optionalLoggedOutSession).isEmpty();

    Optional<UserSession> missingSession = userSessionService.getAndValidateSession("INVALID", true);
    assertThat(missingSession).isEmpty();
  }

  @Test
  public void testIsSessionValid() {
    assertThat(userSessionService.isSessionValid(validSession)).isTrue();
    assertThat(userSessionService.isSessionValid(expiredSession)).isFalse();
    assertThat(userSessionService.isSessionValid(loggedOutSession)).isFalse();
  }

  private UserSession createUserSession(String sessionId, Instant lastAccessTimestamp) {
    UserSession userSession = new UserSession(sessionId);
    AuthenticatedUserAccount userAccount = new AuthenticatedUserAccount(new WebUserAccount(1), List.of());
    userSession.setAuthenticatedUserAccount(userAccount);
    userSession.setLastAccessTimestamp(lastAccessTimestamp);
    return userSession;
  }
}