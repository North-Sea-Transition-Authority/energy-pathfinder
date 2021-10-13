package uk.co.ogauthority.pathfinder.service.teammanagement;


import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.energyportal.model.WebUserAccountStatus;
import uk.co.ogauthority.pathfinder.energyportal.repository.PersonRepository;
import uk.co.ogauthority.pathfinder.energyportal.repository.WebUserAccountRepository;
import uk.co.ogauthority.pathfinder.service.email.TeamManagementEmailService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;

@RunWith(SpringRunner.class)
@DataJpaTest
@DirtiesContext
@ActiveProfiles("integration-test")
public class TeamManagementServiceIntegrationTest {

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private WebUserAccountRepository webUserAccountRepository;

  @Mock
  private TeamService teamService;

  @Mock
  private TeamManagementEmailService teamManagementEmailService;

  @Mock
  private PersonRepository personRepository;

  private TeamManagementService teamManagementService;

  @Before
  public void setup() {
    teamManagementService = new TeamManagementService(
        teamService,
        teamManagementEmailService,
        personRepository,
        webUserAccountRepository
    );
  }

  @Test
  public void getPersonByEmailAddressOrLoginId_whenSearchByEmailAddressAndCaseMatches_assertWebUserAccountFound() {

    var emailAddressLowercase = "someone@example.com";

    createWebUserAccount(emailAddressLowercase);

    var resultingUserAccount = teamManagementService.getPersonByEmailAddressOrLoginId(emailAddressLowercase);

    assertThat(resultingUserAccount).isPresent();
  }

  @Test
  public void getPersonByEmailAddressOrLoginId_whenSearchByEmailAddressAndCaseNotMatching_assertWebUserAccountFound() {

    var emailAddressLowercase = "someone@example.com";
    var emailAddressUppercase = emailAddressLowercase.toUpperCase();

    createWebUserAccount(emailAddressLowercase);

    var resultingUserAccount = teamManagementService.getPersonByEmailAddressOrLoginId(emailAddressUppercase);

    assertThat(resultingUserAccount).isPresent();
  }

  @Test
  public void getPersonByEmailAddressOrLoginId_whenSearchByLoginIdAndCaseMatches_assertWebUserAccountFound() {

    var emailAddress = "someone@example.com";
    var loginIdLowercase = "someone";

    createWebUserAccount(emailAddress, loginIdLowercase);

    var resultingUserAccount = teamManagementService.getPersonByEmailAddressOrLoginId(loginIdLowercase);

    assertThat(resultingUserAccount).isPresent();
  }

  @Test
  public void getPersonByEmailAddressOrLoginId_whenSearchByLoginIdAndCaseNotMatching_assertWebUserAccountFound() {

    var emailAddress = "someone@example.com";
    var loginIdLowercase = "someone";
    var loginIdUppercase = loginIdLowercase.toUpperCase();

    createWebUserAccount(emailAddress, loginIdLowercase);

    var resultingUserAccount = teamManagementService.getPersonByEmailAddressOrLoginId(loginIdUppercase);

    assertThat(resultingUserAccount).isPresent();
  }

  @Test
  public void getPersonByEmailAddressOrLoginId_whenUserNotFound_assertEmptyOptionalResponse() {

    var matchingEmailAddress = "someone@example.com";
    var unmatchedEmailAddress = String.format("%s.uk", matchingEmailAddress);

    createWebUserAccount(matchingEmailAddress);

    var resultingUserAccount = teamManagementService.getPersonByEmailAddressOrLoginId(unmatchedEmailAddress);

    assertThat(resultingUserAccount).isEmpty();
  }

  private void createWebUserAccount(String emailAddress) {
    createWebUserAccount(emailAddress, emailAddress);
  }

  private void createWebUserAccount(String emailAddress, String loginId) {

    var personId = 100;

    createPerson(personId);

    entityManager.createNativeQuery(
        "INSERT INTO user_accounts (" +
            "  wua_id" +
            ", title " +
            ", forename " +
            ", surname " +
            ", email_address " +
            ", login_id " +
            ", id " +
            ", person_id " +
            ", account_status " +
            ") VALUES (" +
            "  :wua_id" +
            ", :title" +
            ", :forename" +
            ", :surname" +
            ", :email_address" +
            ", :login_id" +
            ", :person_id" +
            ", :person_id" +
            ", :account_status" +
            ")"
        )
        .setParameter("wua_id", 1)
        .setParameter("title", "title")
        .setParameter("forename", "forename")
        .setParameter("surname", "surname")
        .setParameter("email_address", emailAddress)
        .setParameter("login_id", loginId)
        .setParameter("person_id", personId)
        .setParameter("account_status", WebUserAccountStatus.ACTIVE.name())
        .executeUpdate();
  }

  private void createPerson(int personId) {
    entityManager.createNativeQuery(
        "INSERT INTO people (" +
            "  id" +
            ", forename" +
            ", surname" +
            ", email_address" +
            ", telephone_no" +
            ") " +
            "VALUES (" +
            "  :person_id" +
            ", :forename" +
            ", :surname" +
            ", :email_address" +
            ", :telephone_no" +
            ")"
        )
        .setParameter("person_id", personId)
        .setParameter("forename", "forename")
        .setParameter("surname", "surname")
        .setParameter("email_address", "email address")
        .setParameter("telephone_no", "telephone no")
        .executeUpdate();
  }
}