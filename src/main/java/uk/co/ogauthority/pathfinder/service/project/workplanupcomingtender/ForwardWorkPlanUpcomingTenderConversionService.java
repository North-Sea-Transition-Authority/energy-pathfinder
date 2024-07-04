package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContract;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractValidationHint;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderConversionForm;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderConversionFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractRepository;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class ForwardWorkPlanUpcomingTenderConversionService {

  private final ForwardWorkPlanAwardedContractRepository awardedContractRepository;
  private final ForwardWorkPlanUpcomingTenderService upcomingTenderService;
  private final ForwardWorkPlanAwardedContractSetupService awardedContractSetupService;
  private final UpcomingTenderConversionFormValidator validator;
  private final ValidationService validationService;

  @Autowired
  public ForwardWorkPlanUpcomingTenderConversionService(ForwardWorkPlanAwardedContractRepository awardedContractRepository,
                                                        ForwardWorkPlanUpcomingTenderService upcomingTenderService,
                                                        ForwardWorkPlanAwardedContractSetupService awardedContractSetupService,
                                                        UpcomingTenderConversionFormValidator validator,
                                                        ValidationService validationService) {
    this.awardedContractRepository = awardedContractRepository;
    this.upcomingTenderService = upcomingTenderService;
    this.awardedContractSetupService = awardedContractSetupService;
    this.validator = validator;
    this.validationService = validationService;
  }

  public BindingResult validate(UpcomingTenderConversionForm form, BindingResult bindingResult) {
    var awardedContractValidationHint = new AwardedContractValidationHint(ValidationType.FULL);
    validator.validate(form, bindingResult, awardedContractValidationHint);
    return validationService.validate(form, bindingResult, ValidationType.FULL);
  }

  @Transactional
  public void convertUpcomingTenderToAwardedContract(ForwardWorkPlanUpcomingTender upcomingTender, UpcomingTenderConversionForm form) {
    var awardedContract = createAwardedContractFromUpcomingTender(upcomingTender, form);

    updateForwardWorkPlanTenderSetup(upcomingTender.getProjectDetail());
    awardedContractRepository.save(awardedContract);
    upcomingTenderService.delete(upcomingTender);
  }

  private ForwardWorkPlanAwardedContract createAwardedContractFromUpcomingTender(ForwardWorkPlanUpcomingTender upcomingTender,
                                                                                 UpcomingTenderConversionForm form) {
    var awardedContract = new ForwardWorkPlanAwardedContract(upcomingTender.getProjectDetail());
    awardedContract.setContractorName(form.getContractorName());
    awardedContract.setDateAwarded(form.getDateAwarded().createDateOrNull());

    var contactDetails = form.getContactDetail();
    awardedContract.setContactName(contactDetails.getName());
    awardedContract.setPhoneNumber(contactDetails.getPhoneNumber());
    awardedContract.setEmailAddress(contactDetails.getEmailAddress());
    awardedContract.setJobTitle(contactDetails.getJobTitle());

    awardedContract.setContractBand(upcomingTender.getContractBand());
    awardedContract.setContractFunction(upcomingTender.getDepartmentType());
    awardedContract.setManualContractFunction(upcomingTender.getManualDepartmentType());
    awardedContract.setDescriptionOfWork(upcomingTender.getDescriptionOfWork());
    awardedContract.setAddedByOrganisationGroup(upcomingTender.getAddedByOrganisationGroup());

    return awardedContract;
  }

  private void updateForwardWorkPlanTenderSetup(ProjectDetail projectDetail) {
    var form = awardedContractSetupService.getAwardedContractSetupFormFromDetail(projectDetail);
    form.setHasContractToAdd(true);

    awardedContractSetupService.saveAwardedContractSetup(form, projectDetail);
  }

}
