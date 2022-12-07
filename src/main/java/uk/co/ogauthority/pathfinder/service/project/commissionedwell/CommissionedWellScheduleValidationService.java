package uk.co.ogauthority.pathfinder.service.project.commissionedwell;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.commissionedwell.CommissionedWellForm;
import uk.co.ogauthority.pathfinder.model.form.project.commissionedwell.CommissionedWellFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.commissionedwell.CommissionedWellValidatorHint;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class CommissionedWellScheduleValidationService {

  private final CommissionedWellFormValidator commissionedWellFormValidator;

  private final ValidationService validationService;

  @Autowired
  public CommissionedWellScheduleValidationService(CommissionedWellFormValidator commissionedWellFormValidator,
                                                   ValidationService validationService) {
    this.commissionedWellFormValidator = commissionedWellFormValidator;
    this.validationService = validationService;
  }

  public BindingResult validate(CommissionedWellForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    commissionedWellFormValidator.validate(form, bindingResult, new CommissionedWellValidatorHint(validationType));
    return validationService.validate(form, bindingResult, validationType);
  }

  boolean isFormValid(CommissionedWellForm commissionedWellForm, ValidationType validationType) {
    BindingResult bindingResult = new BeanPropertyBindingResult(commissionedWellForm, "form");
    bindingResult = validate(commissionedWellForm, bindingResult, validationType);
    return !bindingResult.hasErrors();
  }

  boolean areAllFormsValid(List<CommissionedWellForm> commissionedWellForms, ValidationType validationType) {
    return commissionedWellForms
        .stream()
        .allMatch(commissionedWellForm -> isFormValid(commissionedWellForm, validationType));
  }
}
