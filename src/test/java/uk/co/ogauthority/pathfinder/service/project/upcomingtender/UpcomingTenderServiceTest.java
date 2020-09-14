package uk.co.ogauthority.pathfinder.service.project.upcomingtender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderFormValidator;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.repository.project.upcomingtender.UpcomingTenderRepository;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderUtil;

@RunWith(MockitoJUnitRunner.class)
public class UpcomingTenderServiceTest {

  @Mock
  private UpcomingTenderRepository upcomingTenderRepository;

  @Mock
  private ValidationService validationService;

  @Mock
  private UpcomingTenderFormValidator upcomingTenderFormValidator;

  private UpcomingTenderService upcomingTenderService;

  private final ProjectDetail details = ProjectUtil.getProjectDetails();

  @Before
  public void setUp() {

    SearchSelectorService searchSelectorService = new SearchSelectorService();
    FunctionService functionService = new FunctionService(searchSelectorService);

    upcomingTenderService = new UpcomingTenderService(
        upcomingTenderRepository,
        validationService,
        upcomingTenderFormValidator,
        functionService,
        searchSelectorService
    );

    when(upcomingTenderRepository.save(any(UpcomingTender.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
  }


  @Test
  public void createUpcomingTender() {
    var form = UpcomingTenderUtil.getCompleteForm();
    var newUpcomingTender = upcomingTenderService.createUpcomingTender(details, form);
    assertThat(newUpcomingTender.getProjectDetail()).isEqualTo(details);
    assertThat(newUpcomingTender.getTenderFunction()).isEqualTo(UpcomingTenderUtil.TENDER_FUNCTION);
    checkCommonFields(form, newUpcomingTender);
  }

  @Test
  public void createUpcomingTender_manualFunction() {
    var form = UpcomingTenderUtil.getCompletedForm_manualEntry();
    var newUpcomingTender = upcomingTenderService.createUpcomingTender(details, form);
    assertThat(newUpcomingTender.getProjectDetail()).isEqualTo(details);
    checkCommonFields(form, newUpcomingTender);
  }

  @Test
  public void validate_partial() {
    var form = new UpcomingTenderForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    upcomingTenderService.validate(
        form,
        bindingResult,
        ValidationType.PARTIAL
    );
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.PARTIAL);
  }

  @Test
  public void validate_full() {
    var form = UpcomingTenderUtil.getCompleteForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    upcomingTenderService.validate(
        form,
        bindingResult,
        ValidationType.FULL
    );

    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  private void checkCommonFields(UpcomingTenderForm form, UpcomingTender newUpcomingTender) {
    assertThat(newUpcomingTender.getDescriptionOfWork()).isEqualTo(UpcomingTenderUtil.DESCRIPTION_OF_WORK);
    assertThat(newUpcomingTender.getEstimatedTenderDate()).isEqualTo(form.getEstimatedTenderDate().createDateOrNull());
    assertThat(newUpcomingTender.getContractBand()).isEqualTo(UpcomingTenderUtil.CONTRACT_BAND);
    assertThat(newUpcomingTender.getContactName()).isEqualTo(UpcomingTenderUtil.CONTACT_NAME);
    assertThat(newUpcomingTender.getPhoneNumber()).isEqualTo(UpcomingTenderUtil.PHONE_NUMBER);
    assertThat(newUpcomingTender.getJobTitle()).isEqualTo(UpcomingTenderUtil.JOB_TITLE);
    assertThat(newUpcomingTender.getEmailAddress()).isEqualTo(UpcomingTenderUtil.EMAIL);
  }

  @Test
  public void findTenderFunctionsLikeWithManualEntry() {
    var results = upcomingTenderService.findTenderFunctionsLikeWithManualEntry(Function.FACILITIES_OFFSHORE.getDisplayName());
    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0).getId()).isEqualTo(Function.FACILITIES_OFFSHORE.name());
  }

  @Test
  public void findTenderFunctionsLikeWithManualEntry_withManualEntry() {
    var manualEntry = "manual entry";
    var results = upcomingTenderService.findTenderFunctionsLikeWithManualEntry(manualEntry);
    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0).getId()).isEqualTo(SearchSelectablePrefix.FREE_TEXT_PREFIX+manualEntry);
  }
}
