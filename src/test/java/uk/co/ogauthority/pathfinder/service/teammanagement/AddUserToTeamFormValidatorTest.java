package uk.co.ogauthority.pathfinder.service.teammanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.co.ogauthority.pathfinder.service.teammanagement.AddUserToTeamFormValidator.EMAIL_FIELD;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.form.teammanagement.AddUserToTeamForm;
import uk.co.ogauthority.pathfinder.model.team.Team;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class AddUserToTeamFormValidatorTest {

  @Mock
  private TeamManagementService teamManagementService;

  private AddUserToTeamFormValidator addUserToTeamFormValidator;
  private AddUserToTeamForm addUserToTeamForm;
  private Person foundPerson;
  private Team team;

  @Before
  public void setup() {
    addUserToTeamFormValidator = new AddUserToTeamFormValidator(teamManagementService, "UK Energy Portal");
    addUserToTeamForm = new AddUserToTeamForm();
    team = TeamTestingUtil.getRegulatorTeam();
    foundPerson = new Person(1, "Found", "Person", "found@person.com", "0");
  }

  @Test
  public void validate_emailAddressHasError_whenEmpty() {
    addUserToTeamForm.setEmailAddress("");
    var errors = new BeanPropertyBindingResult(addUserToTeamForm, "form");
    ValidationUtils.invokeValidator(addUserToTeamFormValidator, addUserToTeamForm, errors);

    assertThat(errors.hasFieldErrors(EMAIL_FIELD)).isTrue();
    verifyNoInteractions(teamManagementService);
  }

  @Test
  public void validate_emailAddressHasError_whenNull() {
    addUserToTeamForm.setEmailAddress(null);
    var errors = new BeanPropertyBindingResult(addUserToTeamForm, "form");
    ValidationUtils.invokeValidator(addUserToTeamFormValidator, addUserToTeamForm, errors);

    assertThat(errors.hasFieldErrors(EMAIL_FIELD)).isTrue();
    verifyNoInteractions(teamManagementService);
  }

  @Test
  public void validate_emailAddressHasError_whenPersonNotFound() {
    var identifier = "personWhoDoesNotExist";
    addUserToTeamForm.setEmailAddress(identifier);
    var errors = new BeanPropertyBindingResult(addUserToTeamForm, "form");
    ValidationUtils.invokeValidator(addUserToTeamFormValidator, addUserToTeamForm, errors);

    assertThat(errors.hasFieldErrors(EMAIL_FIELD)).isTrue();
    verify(teamManagementService, times(1)).getPersonByEmailAddress(identifier);
  }

  @Test
  public void validate_emailAddressHasError_whenPersonFound_andAlreadyMemberOfTeam() {
    when(teamManagementService.getPersonByEmailAddress(foundPerson.getEmailAddress()))
        .thenReturn(Optional.of(foundPerson));
    when(teamManagementService.getTeamOrError(team.getId())).thenReturn(team);
    when(teamManagementService.isPersonMemberOfTeam(foundPerson, team)).thenReturn(true);

    addUserToTeamForm.setEmailAddress(foundPerson.getEmailAddress());
    addUserToTeamForm.setResId(team.getId());

    Errors errors = new BeanPropertyBindingResult(addUserToTeamForm, "form");
    ValidationUtils.invokeValidator(addUserToTeamFormValidator, addUserToTeamForm, errors);
    assertThat(errors.hasFieldErrors(EMAIL_FIELD)).isTrue();
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void validate_errorWhenTeamNotfound() {
    when(teamManagementService.getPersonByEmailAddress(foundPerson.getEmailAddress()))
        .thenReturn(Optional.of(foundPerson));
    when(teamManagementService.getTeamOrError(999)).thenThrow(new PathfinderEntityNotFoundException(""));
    addUserToTeamForm.setEmailAddress(foundPerson.getEmailAddress());
    addUserToTeamForm.setResId(999);

    Errors errors = new BeanPropertyBindingResult(addUserToTeamForm, "form");
    ValidationUtils.invokeValidator(addUserToTeamFormValidator, addUserToTeamForm, errors);
  }

  @Test
  public void validate_noErrors_whenPersonFound_andTeamKnown_andNotAlreadyMemberOfTeam() {
    when(teamManagementService.getPersonByEmailAddress(foundPerson.getEmailAddress()))
        .thenReturn(Optional.of(foundPerson));
    when(teamManagementService.getTeamOrError(team.getId())).thenReturn(team);
    when(teamManagementService.isPersonMemberOfTeam(foundPerson, team)).thenReturn(false);
    addUserToTeamForm.setEmailAddress(foundPerson.getEmailAddress());
    addUserToTeamForm.setResId(team.getId());

    Errors errors = new BeanPropertyBindingResult(addUserToTeamForm, "form");
    ValidationUtils.invokeValidator(addUserToTeamFormValidator, addUserToTeamForm, errors);
    assertThat(errors.hasErrors()).isFalse();
  }

}
