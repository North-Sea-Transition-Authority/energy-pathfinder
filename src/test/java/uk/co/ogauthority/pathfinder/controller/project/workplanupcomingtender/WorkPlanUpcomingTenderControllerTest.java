package uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender;

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
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.WorkPlanUpcomingTenderForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.WorkPlanUpcomingTenderService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.WorkPlanUpcomingTenderUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = WorkPlanUpcomingTenderController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class)
)
public class WorkPlanUpcomingTenderControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PROJECT_ID = 1;

  @MockBean
  private WorkPlanUpcomingTenderService workPlanUpcomingTenderService;

  private final AuthenticatedUserAccount unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);


  @Before
  public void setup() {

    when(projectService.getLatestDetailOrError(PROJECT_ID)).thenReturn(projectDetail);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(projectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(projectDetail, unauthenticatedUser)).thenReturn(false);
    when(workPlanUpcomingTenderService.createUpcomingTender(any(), any())).thenReturn(WorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail));
    when(workPlanUpcomingTenderService.getViewUpcomingTendersModelAndView(eq(projectDetail), any())).thenReturn(new ModelAndView(""));
  }

  @Test
  public void authenticatedUser_hasAccessToUpcomingTender() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(WorkPlanUpcomingTenderController.class).viewUpcomingTenders(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessUpcomingTender() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(WorkPlanUpcomingTenderController.class).viewUpcomingTenders(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void authenticatedUser_canAddUpcomingTender() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(WorkPlanUpcomingTenderController.class).addUpcomingTender(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAddUpcomingTender() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(WorkPlanUpcomingTenderController.class).addUpcomingTender(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void saveUpcomingTender_fullValidation_valid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(WorkPlanUpcomingTenderForm.class, "form");
    when(workPlanUpcomingTenderService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(WorkPlanUpcomingTenderController.class)
            .saveUpcomingTender(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(workPlanUpcomingTenderService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(workPlanUpcomingTenderService, times(1)).createUpcomingTender(any(), any());
  }

   @Test
   public void saveUpcomingTender_fullValidation_invalid() throws Exception {
     MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
     add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
     }};

     var bindingResult = new BeanPropertyBindingResult(WorkPlanUpcomingTenderForm.class, "form");
     bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
     when(workPlanUpcomingTenderService.validate(any(), any(), any())).thenReturn(bindingResult);

     mockMvc.perform(
         post(ReverseRouter.route(on(WorkPlanUpcomingTenderController.class)
             .saveUpcomingTender(PROJECT_ID, null, null, null, null)
         ))
             .with(authenticatedUserAndSession(authenticatedUser))
             .with(csrf())
             .params(completeParams))
         .andExpect(status().is2xxSuccessful());

     verify(workPlanUpcomingTenderService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
     verify(workPlanUpcomingTenderService, times(0)).createUpcomingTender(any(), any());
   }

  @Test
  public void saveUpcomingTender_partialValidation_valid() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var bindingResult = new BeanPropertyBindingResult(WorkPlanUpcomingTenderForm.class, "form");
    when(workPlanUpcomingTenderService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(WorkPlanUpcomingTenderController.class)
            .saveUpcomingTender(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(workPlanUpcomingTenderService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(workPlanUpcomingTenderService, times(1)).createUpcomingTender(any(), any());
  }

  @Test
  public void saveUpcomingTender_partialValidation_invalid() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var bindingResult = new BeanPropertyBindingResult(WorkPlanUpcomingTenderForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(workPlanUpcomingTenderService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(WorkPlanUpcomingTenderController.class)
            .saveUpcomingTender(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is2xxSuccessful());

    verify(workPlanUpcomingTenderService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(workPlanUpcomingTenderService, times(0)).createUpcomingTender(any(), any());
  }
}
