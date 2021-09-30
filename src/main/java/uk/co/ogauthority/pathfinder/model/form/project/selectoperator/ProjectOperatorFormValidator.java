package uk.co.ogauthority.pathfinder.model.form.project.selectoperator;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;

@Component
public class ProjectOperatorFormValidator implements SmartValidator {

  public static final String MISSING_PUBLISHABLE_ORGANISATION_ERROR_MESSAGE =
      "Select the operator you want shown on the supply chain interface";

  public static final String ORGANISATION_UNIT_NOT_IN_GROUP_ERROR_MESSAGE =
      "Select an operator who is part of the project operator's group";

  public static final String ORGANISATION_UNIT_NOT_IN_GROUP_ERROR_CODE = ".notInOrganisationGroup";

  public static final String PUBLISHABLE_ORGANISATION_FORM_FIELD_NAME = "publishableOrganisation";

  private final PortalOrganisationAccessor portalOrganisationAccessor;

  @Autowired
  public ProjectOperatorFormValidator(PortalOrganisationAccessor portalOrganisationAccessor) {
    this.portalOrganisationAccessor = portalOrganisationAccessor;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(ProjectOperatorForm.class);
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public void validate(Object target, Errors errors, Object... objects) {

    final var form = (ProjectOperatorForm) target;

    validatePublishableOperatorQuestions(
        errors,
        form.getOperator(),
        form.isPublishedAsOperator(),
        form.getPublishableOrganisation()
    );
  }

  public void validatePublishableOperatorQuestions(Errors errors,
                                                   String projectOperator,
                                                   Boolean isPublishedAsOperator,
                                                   String publishableOrganisation) {
    if (BooleanUtils.isFalse(isPublishedAsOperator)) {

      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          PUBLISHABLE_ORGANISATION_FORM_FIELD_NAME,
          String.format("%s%s", PUBLISHABLE_ORGANISATION_FORM_FIELD_NAME, FieldValidationErrorCodes.INVALID),
          MISSING_PUBLISHABLE_ORGANISATION_ERROR_MESSAGE
      );

      if (StringUtils.isNotBlank(publishableOrganisation) && StringUtils.isNotBlank(projectOperator)) {
        final var organisationExistsWithinGroup = portalOrganisationAccessor.isOrganisationUnitActiveAndPartOfOrganisationGroup(
            Integer.parseInt(projectOperator),
            Integer.parseInt(publishableOrganisation)
        );

        if (!organisationExistsWithinGroup) {
          errors.rejectValue(
              PUBLISHABLE_ORGANISATION_FORM_FIELD_NAME,
              String.format("%s%s", PUBLISHABLE_ORGANISATION_FORM_FIELD_NAME,
                  ORGANISATION_UNIT_NOT_IN_GROUP_ERROR_CODE),
              ORGANISATION_UNIT_NOT_IN_GROUP_ERROR_MESSAGE
          );
        }
      }
    }
  }
}
