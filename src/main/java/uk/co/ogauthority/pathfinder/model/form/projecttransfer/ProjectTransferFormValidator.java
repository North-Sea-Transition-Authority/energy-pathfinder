package uk.co.ogauthority.pathfinder.model.form.projecttransfer;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.ProjectOperatorFormValidator;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;

@Component
public class ProjectTransferFormValidator implements SmartValidator {

  protected static final String SAME_OPERATOR_ERROR_MESSAGE =
      "The new operator/developer must be different to the current operator/developer";

  private final ProjectOperatorFormValidator projectOperatorFormValidator;

  @Autowired
  public ProjectTransferFormValidator(ProjectOperatorFormValidator projectOperatorFormValidator) {
    this.projectOperatorFormValidator = projectOperatorFormValidator;
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (ProjectTransferForm) target;

    ProjectTransferValidationHint projectTransferValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(ProjectTransferValidationHint.class))
        .map(ProjectTransferValidationHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException("Expected ProjectTransferValidationHint to be provided")
        );

    if (form.getNewOrganisationGroup() != null) {
      var newOrganisationGroupId = Integer.parseInt(form.getNewOrganisationGroup());
      if (newOrganisationGroupId == projectTransferValidationHint.getCurrentOrganisationGroup().getOrgGrpId()) {
        errors.rejectValue(
            "newOrganisationGroup",
            "newOrganisationGroup" + FieldValidationErrorCodes.INVALID.getCode(),
            SAME_OPERATOR_ERROR_MESSAGE
        );
      }
    }

    projectOperatorFormValidator.validatePublishableOperatorQuestions(
        errors,
        form.getNewOrganisationGroup(),
        form.isPublishedAsOperator(),
        form.getPublishableOrganisation()
    );
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(ProjectTransferForm.class);
  }
}
