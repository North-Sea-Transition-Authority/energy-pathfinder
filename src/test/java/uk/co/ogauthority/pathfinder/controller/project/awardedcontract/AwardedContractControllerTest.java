package uk.co.ogauthority.pathfinder.controller.project.awardedcontract;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.AwardedContract;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractForm;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.AwardedContractViewUtil;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractSummaryService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = AwardedContractController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class)
)
public class AwardedContractControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PROJECT_ID = 1;
  private static final Integer AWARDED_CONTRACT_ID = 10;

  @MockBean
  private AwardedContractService awardedContractService;

  @MockBean
  private AwardedContractSummaryService awardedContractSummaryService;

  private ProjectDetail projectDetail;

  private AuthenticatedUserAccount authenticatedUser;

  private AuthenticatedUserAccount unauthenticatedUser;

  @Before
  public void setup() {
    projectDetail = ProjectUtil.getProjectDetails();
    authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(SystemAccessService.CREATE_PROJECT_PRIVILEGES);
    unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

    when(projectService.getLatestDetail(PROJECT_ID)).thenReturn(Optional.of(projectDetail));
    when(projectOperatorService.isUserInProjectTeamOrRegulator(projectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(projectDetail, unauthenticatedUser)).thenReturn(false);
  }

  @Test
  public void authenticatedUser_hasAccessToAwardedContractSummary() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(AwardedContractController.class).viewAwardedContracts(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unauthenticatedUser_noAccessToAwardedContractSummary() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(AwardedContractController.class).viewAwardedContracts(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void authenticatedUser_hasAccessToAddAwardedContract() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(AwardedContractController.class).addAwardedContract(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unauthenticatedUser_noAccessToAddAwardedContract() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(AwardedContractController.class).addAwardedContract(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void authenticatedUser_hasAccessToAwardedContract() throws Exception {

    when(awardedContractService.getForm(AWARDED_CONTRACT_ID, projectDetail)).thenReturn(new AwardedContractForm());

    mockMvc.perform(get(ReverseRouter.route(
        on(AwardedContractController.class).getAwardedContract(PROJECT_ID, AWARDED_CONTRACT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unauthenticatedUser_noAccessToAwardedContract() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(AwardedContractController.class).getAwardedContract(PROJECT_ID, AWARDED_CONTRACT_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void saveAwardedContract_partialValidation_validForm() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new AwardedContractForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(awardedContractService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(AwardedContractController.class)
            .saveAwardedContract(PROJECT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(awardedContractService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(awardedContractService, times(1)).createAwardedContract(any(), any());
  }

  @Test
  public void saveAwardedContract_partialValidation_unauthenticated() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new AwardedContractForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(awardedContractService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(AwardedContractController.class)
            .saveAwardedContract(PROJECT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isForbidden());

    verify(awardedContractService, times(0)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(awardedContractService, times(0)).createAwardedContract(any(), any());
  }

  @Test
  public void saveAwardedContract_partialValidation_invalidForm() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new AwardedContractForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(awardedContractService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(AwardedContractController.class)
            .saveAwardedContract(PROJECT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isOk());

    verify(awardedContractService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(awardedContractService, times(0)).createAwardedContract(any(), any());
  }

  @Test
  public void saveAwardedContract_fullValidation_validForm() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new AwardedContractForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(awardedContractService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(AwardedContractController.class)
            .saveAwardedContract(PROJECT_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(awardedContractService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(awardedContractService, times(1)).createAwardedContract(any(), any());
  }

  @Test
  public void saveAwardedContract_fullValidation_unauthenticated() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new AwardedContractForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(awardedContractService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(AwardedContractController.class)
            .saveAwardedContract(PROJECT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isForbidden());

    verify(awardedContractService, times(0)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(awardedContractService, times(0)).createAwardedContract(any(), any());
  }

  @Test
  public void saveAwardedContract_fullValidation_invalidForm() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new AwardedContractForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(awardedContractService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(AwardedContractController.class)
            .saveAwardedContract(PROJECT_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isOk());

    verify(awardedContractService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(awardedContractService, times(0)).createAwardedContract(any(), any());
  }

  @Test
  public void saveAwardedContract_existingContract_partialValidation_validForm() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new AwardedContractForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(awardedContractService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(AwardedContractController.class)
            .saveAwardedContract(PROJECT_ID, AWARDED_CONTRACT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(params))
        .andExpect(status().is3xxRedirection());

    verify(awardedContractService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(awardedContractService, times(1)).updateAwardedContract(eq(AWARDED_CONTRACT_ID), any(), any());
  }

  @Test
  public void saveAwardedContract_existingContract__partialValidation_unauthenticated() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new AwardedContractForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(awardedContractService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(AwardedContractController.class)
            .saveAwardedContract(PROJECT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(params))
        .andExpect(status().isForbidden());

    verify(awardedContractService, times(0)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(awardedContractService, times(0)).updateAwardedContract(eq(AWARDED_CONTRACT_ID), any(), any());
  }

  @Test
  public void saveAwardedContract_existingContract__partialValidation_invalidForm() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new AwardedContractForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(awardedContractService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(AwardedContractController.class)
            .saveAwardedContract(PROJECT_ID, AWARDED_CONTRACT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(params))
        .andExpect(status().isOk());

    verify(awardedContractService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(awardedContractService, times(0)).updateAwardedContract(eq(AWARDED_CONTRACT_ID), any(), any());
  }

  @Test
  public void saveAwardedContract_existingContract__fullValidation_validForm() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new AwardedContractForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(awardedContractService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(AwardedContractController.class)
            .saveAwardedContract(PROJECT_ID, AWARDED_CONTRACT_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(params))
        .andExpect(status().is3xxRedirection());

    verify(awardedContractService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(awardedContractService, times(1)).updateAwardedContract(eq(AWARDED_CONTRACT_ID), any(), any());
  }

  @Test
  public void saveAwardedContract_existingContract__fullValidation_unauthenticated() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new AwardedContractForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(awardedContractService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(AwardedContractController.class)
            .saveAwardedContract(PROJECT_ID, AWARDED_CONTRACT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(params))
        .andExpect(status().isForbidden());

    verify(awardedContractService, times(0)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(awardedContractService, times(0)).updateAwardedContract(eq(AWARDED_CONTRACT_ID), any(), any());
  }

  @Test
  public void saveAwardedContract_existingContract_fullValidation_invalidForm() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new AwardedContractForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(awardedContractService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(AwardedContractController.class)
            .saveAwardedContract(PROJECT_ID, AWARDED_CONTRACT_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(params))
        .andExpect(status().isOk());

    verify(awardedContractService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(awardedContractService, times(0)).updateAwardedContract(eq(AWARDED_CONTRACT_ID), any(), any());
  }

  @Test
  public void removeAwardedContractConfirmation_authenticated_thenValid() throws Exception {

    var awardedContractView = AwardedContractViewUtil.from(
        AwardedContractTestUtil.createAwardedContract(),
        1
    );

    when(awardedContractSummaryService.getAwardedContractView(AWARDED_CONTRACT_ID, projectDetail, 1))
        .thenReturn(awardedContractView);

    mockMvc.perform(
        get(ReverseRouter.route(on(AwardedContractController.class)
            .removeAwardedContractConfirmation(PROJECT_ID, AWARDED_CONTRACT_ID, 1, null))
        )
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void removeAwardedContractConfirmation_unauthenticated_thenInvalid() throws Exception {
    mockMvc.perform(
        get(ReverseRouter.route(on(AwardedContractController.class)
            .removeAwardedContractConfirmation(PROJECT_ID, AWARDED_CONTRACT_ID, 1, null))
        )
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void removeAwardedContract_unauthenticated_thenInvalid() throws Exception {
    mockMvc.perform(
        post(ReverseRouter.route(on(AwardedContractController.class)
            .removeAwardedContract(PROJECT_ID, AWARDED_CONTRACT_ID, 1, null))
        )
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());

    verify(awardedContractService, times(0)).deleteAwardedContract(any());
  }

  @Test
  public void removeAwardedContract_authenticated_thenValid() throws Exception {

    AwardedContract awardedContract = AwardedContractTestUtil.createAwardedContract();
    awardedContract.setProjectDetail(projectDetail);

    when(awardedContractService.getAwardedContract(AWARDED_CONTRACT_ID, projectDetail))
        .thenReturn(awardedContract);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    mockMvc.perform(
        post(ReverseRouter.route(on(AwardedContractController.class)
            .removeAwardedContract(
                PROJECT_ID,
                AWARDED_CONTRACT_ID,
                1,
                null
            ))
        )
        .with(authenticatedUserAndSession(authenticatedUser))
        .with(csrf())
        .params(params))
        .andExpect(status().is3xxRedirection());

    verify(awardedContractService, times(1)).deleteAwardedContract(awardedContract);
  }

  @Test
  public void saveAwardedContractSummary_authenticatedAndAllValid_thenRedirect() throws Exception {

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    when(awardedContractSummaryService.validateViews(any())).thenReturn(ValidationResult.VALID);

    mockMvc.perform(
        post(ReverseRouter.route(on(AwardedContractController.class)
            .saveAwardedContractSummary(PROJECT_ID, null))
        )
        .with(authenticatedUserAndSession(authenticatedUser))
        .with(csrf())
        .params(params))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  public void saveAwardedContractSummary_authenticatedAndAllInvalid_thenStayOnSummary() throws Exception {

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    when(awardedContractSummaryService.validateViews(any())).thenReturn(ValidationResult.INVALID);

    mockMvc.perform(
        post(ReverseRouter.route(on(AwardedContractController.class)
            .saveAwardedContractSummary(PROJECT_ID, null))
        )
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(params))
        .andExpect(status().isOk());
  }

  @Test
  public void saveAwardedContractSummary_unauthenticated_thenInvalid() throws Exception {

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    mockMvc.perform(
        post(ReverseRouter.route(on(AwardedContractController.class)
            .saveAwardedContractSummary(PROJECT_ID, null))
        )
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(params))
        .andExpect(status().isForbidden());
  }
}