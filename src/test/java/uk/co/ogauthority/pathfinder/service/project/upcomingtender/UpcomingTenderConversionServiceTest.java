package uk.co.ogauthority.pathfinder.service.project.upcomingtender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.infrastructure.InfrastructureAwardedContract;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.tasklistquestions.TaskListSectionAnswer;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractValidationHint;
import uk.co.ogauthority.pathfinder.model.form.project.setup.ProjectSetupForm;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderConversionFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.infrastructure.InfrastructureAwardedContractRepository;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderConversionUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderUtil;

@ExtendWith(MockitoExtension.class)
class UpcomingTenderConversionServiceTest {

  @Mock
  private InfrastructureAwardedContractRepository awardedContractRepository;

  @Mock
  private UpcomingTenderService upcomingTenderService;

  @Mock
  private ProjectSetupService projectSetupService;

  @Mock
  private UpcomingTenderConversionFormValidator validator;

  @Mock
  private ValidationService validationService;

  @InjectMocks
  private UpcomingTenderConversionService conversionService;

  @Captor
  private ArgumentCaptor<InfrastructureAwardedContract> awardedContractCaptor;

  @Test
  void validate() {
    var form = UpcomingTenderConversionUtil.createUpcomingTenderConversionForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    conversionService.validate(form, bindingResult);
    verify(validator).validate(eq(form), eq(bindingResult), any(AwardedContractValidationHint.class));
    verify(validationService).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  void convertUpcomingTenderToAwardedContract() {
    var projectDetail = ProjectUtil.getProjectDetails();
    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(projectDetail);
    var upcomingTenderConversionForm = UpcomingTenderConversionUtil.createUpcomingTenderConversionForm();

    var projectSetupForm = new ProjectSetupForm();
    projectSetupForm.setUpcomingTendersIncluded(TaskListSectionAnswer.UPCOMING_TENDERS_YES);
    when(projectSetupService.getForm(projectDetail)).thenReturn(projectSetupForm);

    conversionService.convertUpcomingTenderToAwardedContract(upcomingTender, upcomingTenderConversionForm);

    verify(projectSetupService).createOrUpdateProjectTaskListSetup(projectDetail, projectSetupForm);
    assertThat(projectSetupForm.getAwardedContractsIncluded()).isEqualTo(TaskListSectionAnswer.AWARDED_CONTRACTS_YES);

    verify(awardedContractRepository).save(awardedContractCaptor.capture());
    var awardedContract = awardedContractCaptor.getValue();
    assertThat(awardedContract.getContractorName()).isEqualTo(upcomingTenderConversionForm.getContractorName());
    assertThat(awardedContract.getDateAwarded()).isEqualTo(upcomingTenderConversionForm.getDateAwarded().createDateOrNull());
    assertThat(awardedContract.getContactName()).isEqualTo(upcomingTender.getContactName());
    assertThat(awardedContract.getContractBand()).isEqualTo(upcomingTender.getContractBand());
    assertThat(awardedContract.getContractFunction()).isEqualTo(upcomingTender.getTenderFunction());
    assertThat(awardedContract.getManualContractFunction()).isEqualTo(upcomingTender.getManualTenderFunction());
    assertThat(awardedContract.getDescriptionOfWork()).isEqualTo(upcomingTender.getDescriptionOfWork());
    assertThat(awardedContract.getPhoneNumber()).isEqualTo(upcomingTender.getPhoneNumber());
    assertThat(awardedContract.getEmailAddress()).isEqualTo(upcomingTender.getEmailAddress());
    assertThat(awardedContract.getJobTitle()).isEqualTo(upcomingTender.getJobTitle());
    assertThat(awardedContract.getAddedByOrganisationGroup()).isEqualTo(upcomingTender.getAddedByOrganisationGroup());

    verify(upcomingTenderService).delete(upcomingTender);
  }
}
