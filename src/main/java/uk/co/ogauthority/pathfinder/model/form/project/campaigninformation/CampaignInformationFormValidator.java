package uk.co.ogauthority.pathfinder.model.form.project.campaigninformation;

import java.util.Arrays;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.service.project.campaigninformation.CampaignProjectValidatorService;

@Component
public class CampaignInformationFormValidator implements SmartValidator {

  private final CampaignProjectValidatorService campaignProjectValidatorService;

  @Autowired
  public CampaignInformationFormValidator(CampaignProjectValidatorService campaignProjectValidatorService) {
    this.campaignProjectValidatorService = campaignProjectValidatorService;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(CampaignInformationForm.class);
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {

    final var form = (CampaignInformationForm) target;

    var campaignInformationValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(CampaignInformationValidationHint.class))
        .map(CampaignInformationValidationHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException(
                "Expected CampaignInformationValidationHint to be provided"
            )
        );

    if (BooleanUtils.isTrue(form.isPartOfCampaign())) {

      if (
          campaignInformationValidationHint.getValidationType().equals(ValidationType.FULL)
          &&
          form.getProjectsIncludedInCampaign().isEmpty()
      ) {
        errors.rejectValue(
            campaignInformationValidationHint.getProjectSelectorFieldName(),
            campaignInformationValidationHint.getProjectSelectorErrorCode(),
            campaignInformationValidationHint.getProjectSelectorErrorMessage()
        );
      } else if (!form.getProjectsIncludedInCampaign().isEmpty()) {
        campaignProjectValidatorService.validateCampaignProjects(
            campaignInformationValidationHint.getCurrentProject(),
            form.getProjectsIncludedInCampaign(),
            errors
        );
      }
    }

  }
}
