package uk.co.ogauthority.pathfinder.controller.project.projectinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStageSubCategory;
import uk.co.ogauthority.pathfinder.model.form.project.projectinformation.ProjectInformationForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ProjectInformationController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class))
public class ProjectInformationControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PROJECT_ID = 1;


  @MockitoBean
  private ProjectInformationService projectInformationService;

  @MockitoBean
  private ProjectSetupService projectSetupService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();


  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  private static final AuthenticatedUserAccount unAuthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setUp() throws Exception {
    when(projectService.getLatestDetailOrError(PROJECT_ID)).thenReturn(detail);
    when(projectOperatorService.isUserInProjectTeam(detail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeam(detail, unAuthenticatedUser)).thenReturn(false);
  }

  @Test
  public void authenticatedUser_hasAccessToProjectInformation() throws Exception {
    var form = new ProjectInformationForm();
    when(projectInformationService.getForm(detail)).thenReturn(form);
    var modelAndView = mockMvc.perform(get(ReverseRouter.route(
        on(ProjectInformationController.class).getProjectInformation(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk())
        .andExpect(view().name("project/projectinformation/projectInformation"))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();
    var model = modelAndView.getModel();

    assertThat(model)
        .containsEntry("form", form)
        .containsEntry("pageName", ProjectInformationController.PAGE_NAME)
        .containsEntry("carbonCaptureAndStorageFieldStage", FieldStage.getEntryAsMap(FieldStage.CARBON_CAPTURE_AND_STORAGE))
        .containsEntry("carbonAndOnshoreCategory", FieldStageSubCategory.getEntryAsMap(FieldStageSubCategory.CAPTURE_AND_ONSHORE))
        .containsEntry("carbonAndOnshoreDescription", FieldStageSubCategory.CAPTURE_AND_ONSHORE.getDescription())
        .containsEntry("transportationAndStorageCategory", FieldStageSubCategory.getEntryAsMap(FieldStageSubCategory.TRANSPORTATION_AND_STORAGE))
        .containsEntry("transportationAndStorageDescription", FieldStageSubCategory.TRANSPORTATION_AND_STORAGE.getDescription())
        .containsEntry("hydrogenFieldStage", FieldStage.getEntryAsMap(FieldStage.HYDROGEN))
        .containsEntry("offshoreHydrogenCategory", FieldStageSubCategory.getEntryAsMap(FieldStageSubCategory.OFFSHORE_HYDROGEN))
        .containsEntry("onshoreHydrogenCategory", FieldStageSubCategory.getEntryAsMap(FieldStageSubCategory.ONSHORE_HYDROGEN))
        .containsEntry("electrificationFieldStage", FieldStage.getEntryAsMap(FieldStage.ELECTRIFICATION))
        .containsEntry("offshoreElectrificationCategory", FieldStageSubCategory.getEntryAsMap(FieldStageSubCategory.OFFSHORE_ELECTRIFICATION))
        .containsEntry("onshoreElectrificationCategory", FieldStageSubCategory.getEntryAsMap(FieldStageSubCategory.ONSHORE_ELECTRIFICATION))
        .containsEntry("oilAndGasFieldStage", FieldStage.getEntryAsMap(FieldStage.OIL_AND_GAS))
        .containsEntry("discoveryCategory", FieldStageSubCategory.getEntryAsMap(FieldStageSubCategory.DISCOVERY))
        .containsEntry("discoveryCategoryDescription", FieldStageSubCategory.DISCOVERY.getDescription())
        .containsEntry("developmentCategory", FieldStageSubCategory.getEntryAsMap(FieldStageSubCategory.DEVELOPMENT))
        .containsEntry("developmentCategoryDescription", FieldStageSubCategory.DEVELOPMENT.getDescription())
        .containsEntry("decommissioningCategory", FieldStageSubCategory.getEntryAsMap(FieldStageSubCategory.DECOMMISSIONING))
        .containsEntry("decommissioningCategoryDescription", FieldStageSubCategory.DECOMMISSIONING.getDescription())
        .containsEntry("windEnergyFieldStage", FieldStage.getEntryAsMap(FieldStage.WIND_ENERGY))
        .containsEntry("fixedBottomOffshoreWindCategory", FieldStageSubCategory.getEntryAsMap(FieldStageSubCategory.FIXED_BOTTOM_OFFSHORE_WIND))
        .containsEntry("floatingOffshoreWindCategory", FieldStageSubCategory.getEntryAsMap(FieldStageSubCategory.FLOATING_OFFSHORE_WIND))
        .containsEntry("onshoreWindCategory", FieldStageSubCategory.getEntryAsMap(FieldStageSubCategory.ONSHORE_WIND))
        .containsEntry("quarters", Quarter.getAllAsMap());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessProjectInformation() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ProjectInformationController.class).getProjectInformation(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void saveProjectInformation_partialValidation() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var bindingResult = new BeanPropertyBindingResult(ProjectInformationForm.class, "form");
    when(projectInformationService.validate(any(), any(), any())).thenReturn(bindingResult);

    var projectInformationEntity = ProjectInformationUtil.getProjectInformation_withCompleteDetails(detail);
    when(projectInformationService.createOrUpdate(any(), any())).thenReturn(projectInformationEntity);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectInformationController.class)
            .saveProjectInformation(PROJECT_ID, null, null, null, null)
          ))
          .with(authenticatedUserAndSession(authenticatedUser))
          .with(csrf())
          .params(completeLaterParams))
      .andExpect(status().is3xxRedirection());

    verify(projectInformationService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(projectInformationService, times(1)).createOrUpdate(any(), any());
    verify(projectSetupService, times(1)).removeTaskListSetupSectionsNotApplicableToFieldStageAndSubCategory(
        detail,
        projectInformationEntity.getFieldStage(),
        projectInformationEntity.getFieldStageSubCategory()
    );
  }



  @Test
  public void saveProjectInformation_fullValidation_invalid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(ProjectInformationForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(projectInformationService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectInformationController.class)
            .saveProjectInformation(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is2xxSuccessful());

    verify(projectInformationService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(projectInformationService, times(0)).createOrUpdate(any(), any());
    verify(projectSetupService, never()).removeTaskListSetupSectionsNotApplicableToFieldStageAndSubCategory(
        eq(detail),
        any(),
        any()
    );
  }

  @Test
  public void saveProjectInformation_fullValidation_valid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
      add("fieldStage", "DISCOVERY");
      add("projectTitle", "Project title");
      add("projectSummary", "Project summary");
    }};

    var bindingResult = new BeanPropertyBindingResult(ProjectInformationForm.class, "form");
    when(projectInformationService.validate(any(), any(), any())).thenReturn(bindingResult);

    var projectInformationEntity = ProjectInformationUtil.getProjectInformation_withCompleteDetails(detail);
    when(projectInformationService.createOrUpdate(any(), any())).thenReturn(projectInformationEntity);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectInformationController.class)
            .saveProjectInformation(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(projectInformationService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(projectInformationService, times(1)).createOrUpdate(any(), any());

    verify(projectSetupService, times(1)).removeTaskListSetupSectionsNotApplicableToFieldStageAndSubCategory(
        detail,
        projectInformationEntity.getFieldStage(),
        projectInformationEntity.getFieldStageSubCategory()
    );
  }
}
