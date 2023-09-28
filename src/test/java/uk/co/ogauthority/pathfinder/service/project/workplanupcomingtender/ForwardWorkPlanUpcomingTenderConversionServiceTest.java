package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

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
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContract;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractValidationHint;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupForm;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderConversionFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractRepository;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanUpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderConversionUtil;

@ExtendWith(MockitoExtension.class)
class ForwardWorkPlanUpcomingTenderConversionServiceTest {

  @Mock
  private ForwardWorkPlanAwardedContractRepository awardedContractRepository;

  @Mock
  private ForwardWorkPlanUpcomingTenderService upcomingTenderService;

  @Mock
  private ForwardWorkPlanAwardedContractSetupService awardedContractSetupService;

  @Mock
  private UpcomingTenderConversionFormValidator validator;

  @Mock
  private ValidationService validationService;

  @InjectMocks
  private ForwardWorkPlanUpcomingTenderConversionService conversionService;

  @Captor
  private ArgumentCaptor<ForwardWorkPlanAwardedContract> awardedContractCaptor;

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
    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    var upcomingTenderConversionForm = UpcomingTenderConversionUtil.createUpcomingTenderConversionForm();

    var awardedContractSetupForm = new ForwardWorkPlanAwardedContractSetupForm();
    awardedContractSetupForm.setHasContractToAdd(false);
    when(awardedContractSetupService.getAwardedContractSetupFormFromDetail(projectDetail))
        .thenReturn(awardedContractSetupForm);

    conversionService.convertUpcomingTenderToAwardedContract(upcomingTender, upcomingTenderConversionForm);

    verify(awardedContractSetupService).saveAwardedContractSetup(awardedContractSetupForm, projectDetail);
    assertThat(awardedContractSetupForm.getHasContractToAdd()).isTrue();

    verify(awardedContractRepository).save(awardedContractCaptor.capture());
    var awardedContract = awardedContractCaptor.getValue();
    assertThat(awardedContract.getContractorName()).isEqualTo(upcomingTenderConversionForm.getContractorName());
    assertThat(awardedContract.getDateAwarded()).isEqualTo(upcomingTenderConversionForm.getDateAwarded().createDateOrNull());
    assertThat(awardedContract.getContactName()).isEqualTo(upcomingTenderConversionForm.getContactDetail().getName());
    assertThat(awardedContract.getContractBand()).isEqualTo(upcomingTender.getContractBand());
    assertThat(awardedContract.getContractFunction()).isEqualTo(upcomingTender.getDepartmentType());
    assertThat(awardedContract.getManualContractFunction()).isEqualTo(upcomingTender.getManualDepartmentType());
    assertThat(awardedContract.getDescriptionOfWork()).isEqualTo(upcomingTender.getDescriptionOfWork());
    assertThat(awardedContract.getPhoneNumber()).isEqualTo(upcomingTenderConversionForm.getContactDetail().getPhoneNumber());
    assertThat(awardedContract.getEmailAddress()).isEqualTo(upcomingTenderConversionForm.getContactDetail().getEmailAddress());
    assertThat(awardedContract.getJobTitle()).isEqualTo(upcomingTenderConversionForm.getContactDetail().getJobTitle());
    assertThat(awardedContract.getAddedByOrganisationGroup()).isEqualTo(upcomingTender.getAddedByOrganisationGroup());

    verify(upcomingTenderService).delete(upcomingTender);
  }
}
