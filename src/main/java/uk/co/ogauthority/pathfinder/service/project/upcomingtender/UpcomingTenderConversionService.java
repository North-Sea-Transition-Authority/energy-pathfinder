package uk.co.ogauthority.pathfinder.service.project.upcomingtender;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.infrastructure.InfrastructureAwardedContract;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions.TaskListSectionAnswer;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractValidationHint;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderConversionForm;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderConversionFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.infrastructure.InfrastructureAwardedContractRepository;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class UpcomingTenderConversionService {

  private final ProjectSetupService projectSetupService;
  private final InfrastructureAwardedContractRepository awardedContractRepository;
  private final UpcomingTenderService upcomingTenderService;
  private final UpcomingTenderConversionFormValidator validator;
  private final ValidationService validationService;

  @Autowired
  public UpcomingTenderConversionService(ProjectSetupService projectSetupService,
                                         InfrastructureAwardedContractRepository awardedContractRepository,
                                         UpcomingTenderService upcomingTenderService,
                                         UpcomingTenderConversionFormValidator validator,
                                         ValidationService validationService) {
    this.projectSetupService = projectSetupService;
    this.awardedContractRepository = awardedContractRepository;
    this.upcomingTenderService = upcomingTenderService;
    this.validator = validator;
    this.validationService = validationService;
  }

  public BindingResult validate(UpcomingTenderConversionForm form, BindingResult bindingResult) {
    var awardedContractValidationHint = new AwardedContractValidationHint(ValidationType.FULL);
    validator.validate(form, bindingResult, awardedContractValidationHint);
    return validationService.validate(form, bindingResult, ValidationType.FULL);
  }

  @Transactional
  public void convertUpcomingTenderToAwardedContract(UpcomingTender upcomingTender, UpcomingTenderConversionForm form) {
    var awardedContract = createAwardedContractFromUpcomingTender(upcomingTender, form);

    addAwardedContractSectionToProject(upcomingTender.getProjectDetail());
    awardedContractRepository.save(awardedContract);
    upcomingTenderService.delete(upcomingTender);
  }

  private InfrastructureAwardedContract createAwardedContractFromUpcomingTender(UpcomingTender upcomingTender,
                                                                                UpcomingTenderConversionForm form) {
    var awardedContract = new InfrastructureAwardedContract(upcomingTender.getProjectDetail());
    awardedContract.setContractorName(form.getContractorName());
    awardedContract.setDateAwarded(form.getDateAwarded().createDateOrNull());

    var contactDetails = form.getContactDetail();
    awardedContract.setContactName(contactDetails.getName());
    awardedContract.setPhoneNumber(contactDetails.getPhoneNumber());
    awardedContract.setEmailAddress(contactDetails.getEmailAddress());
    awardedContract.setJobTitle(contactDetails.getJobTitle());

    awardedContract.setContractBand(upcomingTender.getContractBand());
    awardedContract.setContractFunction(upcomingTender.getTenderFunction());
    awardedContract.setManualContractFunction(upcomingTender.getManualTenderFunction());
    awardedContract.setDescriptionOfWork(upcomingTender.getDescriptionOfWork());
    awardedContract.setAddedByOrganisationGroup(upcomingTender.getAddedByOrganisationGroup());

    return awardedContract;
  }

  private void addAwardedContractSectionToProject(ProjectDetail projectDetail) {
    var projectSetupForm = projectSetupService.getForm(projectDetail);
    projectSetupForm.setAwardedContractsIncluded(TaskListSectionAnswer.AWARDED_CONTRACTS_YES);

    projectSetupService.createOrUpdateProjectTaskListSetup(projectDetail, projectSetupForm);
  }
}
