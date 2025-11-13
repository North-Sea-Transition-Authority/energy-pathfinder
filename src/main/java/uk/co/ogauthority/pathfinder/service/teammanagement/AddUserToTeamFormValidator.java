package uk.co.ogauthority.pathfinder.service.teammanagement;

import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.model.form.teammanagement.AddUserToTeamForm;
import uk.co.ogauthority.pathfinder.model.team.Team;

@Service
public class AddUserToTeamFormValidator implements Validator {

  static final String EMAIL_FIELD = "emailAddress";

  private final TeamManagementService teamManagementService;
  private final String energyPortalName;

  @Autowired
  public AddUserToTeamFormValidator(
      TeamManagementService teamManagementService,
      @Value("${energy-portal.name}") String energyPortalName
  ) {
    this.teamManagementService = teamManagementService;
    this.energyPortalName = energyPortalName;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return AddUserToTeamForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    AddUserToTeamForm form = (AddUserToTeamForm) target;

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, EMAIL_FIELD, "%s.required".formatted(EMAIL_FIELD),
        "Enter an email address");

    if (StringUtils.isNotEmpty(form.getEmailAddress())) {

      Optional<Person> person = teamManagementService.getPersonByEmailAddress(form.getEmailAddress());

      if (person.isEmpty()) {
        errors.rejectValue(
            EMAIL_FIELD,
            "%s.userNotFound".formatted(EMAIL_FIELD),
            "No %s user exists with this email address".formatted(energyPortalName)
        );
      } else {
        // check if the person is already member of the team
        Team team = teamManagementService.getTeamOrError(form.getResId());
        Person teamUser = person.get();
        if (teamManagementService.isPersonMemberOfTeam(teamUser, team)) {
          errors.rejectValue(
              EMAIL_FIELD,
              "%s.userAlreadyExists".formatted(EMAIL_FIELD),
              "This person is already a member of this team"
          );
        }
      }
    }
  }
}