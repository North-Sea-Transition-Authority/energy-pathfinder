package uk.co.ogauthority.pathfinder.service.teammanagement;

import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.model.form.teammanagement.AddUserToTeamForm;
import uk.co.ogauthority.pathfinder.model.team.Team;

@Service
public class AddUserToTeamFormValidator implements Validator {

  private final TeamManagementService teamManagementService;

  @Autowired
  public AddUserToTeamFormValidator(TeamManagementService teamManagementService) {
    this.teamManagementService = teamManagementService;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return AddUserToTeamForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    AddUserToTeamForm form = (AddUserToTeamForm) target;

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userIdentifier", "userIdentifier.required",
        "Enter an email address or login ID");

    if (StringUtils.isNotEmpty(form.getUserIdentifier())) {

      Optional<Person> person = teamManagementService.getPersonByEmailAddressOrLoginId(form.getUserIdentifier());

      if (person.isEmpty()) {
        errors.rejectValue(
            "userIdentifier",
            "userIdentifier.userNotFound",
            "No Energy Portal user exists with this email address or login ID"
        );
      } else {
        // check if the person is already member of the team
        Team team = teamManagementService.getTeamOrError(form.getResId());
        Person teamUser = person.get();
        if (teamManagementService.isPersonMemberOfTeam(teamUser, team)) {
          errors.rejectValue("userIdentifier", "userIdentifier.userAlreadyExists", "This person is already a member of this team");
        }
      }
    }
  }
}