package uk.co.ogauthority.pathfinder.controller.project.upcomingtender;

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

import java.sql.SQLException;
import java.util.Collections;
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
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.file.UploadedFile;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.view.upcomingtender.UpcomingTenderViewUtil;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderService;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderSummaryService;
import uk.co.ogauthority.pathfinder.testutil.ProjectFileTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UpcomingTendersController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class))
public class UpcomingTenderControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PROJECT_ID = 1;
  private static final Integer UPCOMING_TENDER_ID = 1;
  private static final Integer DISPLAY_ORDER = 1;

  @MockBean
  private UpcomingTenderService upcomingTenderService;

  @MockBean
  private UpcomingTenderSummaryService upcomingTenderSummaryService;

  @MockBean
  ProjectDetailFileService projectDetailFileService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private final ProjectDetailFile PROJECT_DETAIL_FILE = ProjectFileTestUtil.getProjectDetailFile(detail);


  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  private static final AuthenticatedUserAccount unAuthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  private final UpcomingTender upcomingTender = UpcomingTenderUtil.getUpcomingTender(detail);

  @Before
  public void setUp() throws SQLException {
    when(projectService.getLatestDetailOrError(PROJECT_ID)).thenReturn(detail);
    when(upcomingTenderService.getOrError(UPCOMING_TENDER_ID)).thenReturn(upcomingTender);
    UploadedFile file = ProjectFileTestUtil.getUploadedFile();

    var upcomingTenderView = UpcomingTenderViewUtil.createUpComingTenderView(
        upcomingTender,
        DISPLAY_ORDER,
        Collections.emptyList()
    );
    when(upcomingTenderSummaryService.getUpcomingTenderView(upcomingTender, DISPLAY_ORDER)).thenReturn(upcomingTenderView);

    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, unAuthenticatedUser)).thenReturn(false);
    when(upcomingTenderService.createUpcomingTender(any(), any(), any())).thenReturn(UpcomingTenderUtil.getUpcomingTender(detail));
    when(upcomingTenderService.updateUpcomingTender(any(), any(), any())).thenReturn(UpcomingTenderUtil.getUpcomingTender(detail));
    when(projectDetailFileService.getProjectDetailFileByProjectDetailAndFileId(any(), any())).thenReturn(PROJECT_DETAIL_FILE);
    when(projectDetailFileService.getUploadedFileById(ProjectFileTestUtil.FILE_ID)).thenReturn(file);
  }


  @Test
  public void authenticatedUser_hasAccessToUpcomingTender() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(UpcomingTendersController.class).addUpcomingTender(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessUpcomingTender() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(UpcomingTendersController.class).addUpcomingTender(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void authenticatedUser_hasAccessToUpcomingTenderSummary() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(UpcomingTendersController.class).viewUpcomingTenders(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessUpcomingTenderSummary() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(UpcomingTendersController.class).viewUpcomingTenders(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void authenticatedUser_hasAccessToUpcomingTenderRemove() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(UpcomingTendersController.class).removeUpcomingTenderConfirm(PROJECT_ID, UPCOMING_TENDER_ID, DISPLAY_ORDER, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessUpcomingTenderRemove() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(UpcomingTendersController.class).removeUpcomingTenderConfirm(PROJECT_ID, UPCOMING_TENDER_ID, DISPLAY_ORDER, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void authenticatedUser_hasAccessToUpcomingTenderEdit() throws Exception {
    when(upcomingTenderService.getForm(upcomingTender)).thenReturn(UpcomingTenderUtil.getCompleteForm());
    mockMvc.perform(get(ReverseRouter.route(
        on(UpcomingTendersController.class).editUpcomingTender(PROJECT_ID, UPCOMING_TENDER_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessUpcomingTenderEdit() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(UpcomingTendersController.class).editUpcomingTender(PROJECT_ID, UPCOMING_TENDER_ID, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void saveUpcomingTender_partialValidation() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var bindingResult = new BeanPropertyBindingResult(UpcomingTenderForm.class, "form");
    when(upcomingTenderService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(UpcomingTendersController.class)
            .saveUpcomingTender(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(upcomingTenderService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(upcomingTenderService, times(1)).createUpcomingTender(any(), any(), any());
  }

  @Test
  public void saveUpcomingTender_fullValidation_invalid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(UpcomingTenderForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(upcomingTenderService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(UpcomingTendersController.class)
            .saveUpcomingTender(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is2xxSuccessful());

    verify(upcomingTenderService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(upcomingTenderService, times(0)).createUpcomingTender(any(), any(), any());
  }

  @Test
  public void saveUpcomingTender_fullValidation_valid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(UpcomingTenderForm.class, "form");
    when(upcomingTenderService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(UpcomingTendersController.class)
            .saveUpcomingTender(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(upcomingTenderService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(upcomingTenderService, times(1)).createUpcomingTender(any(), any(), any());
  }

  @Test
  public void updateUpcomingTender_partialValidation() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var bindingResult = new BeanPropertyBindingResult(UpcomingTenderForm.class, "form");
    when(upcomingTenderService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(UpcomingTendersController.class)
            .updateUpcomingTender(PROJECT_ID, UPCOMING_TENDER_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(upcomingTenderService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(upcomingTenderService, times(1)).updateUpcomingTender(any(), any(), any());
  }

  @Test
  public void updateUpcomingTender_fullValidation_invalid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(UpcomingTenderForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(upcomingTenderService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(UpcomingTendersController.class)
            .updateUpcomingTender(PROJECT_ID, UPCOMING_TENDER_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is2xxSuccessful());

    verify(upcomingTenderService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(upcomingTenderService, times(0)).updateUpcomingTender(any(), any(), any());
  }

  @Test
  public void updateUpcomingTender_fullValidation_valid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(UpcomingTenderForm.class, "form");
    when(upcomingTenderService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(UpcomingTendersController.class)
            .updateUpcomingTender(PROJECT_ID, UPCOMING_TENDER_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(upcomingTenderService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(upcomingTenderService, times(1)).updateUpcomingTender(any(), any(), any());
  }

  @Test
  public void handleDownload_draftProject() throws Exception {
    makeDownloadRequest(ProjectStatus.DRAFT);
  }

  @Test
  public void handleDownload_qaProject() throws Exception {
    makeDownloadRequest(ProjectStatus.QA);
  }

  @Test
  public void handleDownload_publishedProject() throws Exception {
    makeDownloadRequest(ProjectStatus.PUBLISHED);
  }

  @Test
  public void handleDownload_archivedProject() throws Exception {
    makeDownloadRequest(ProjectStatus.ARCHIVED);
  }

  private void makeDownloadRequest(ProjectStatus status) throws Exception {
    var customStatusProject = detail;
    customStatusProject.setStatus(status);
    when(projectService.getLatestDetailOrError(PROJECT_ID)).thenReturn(customStatusProject);
    mockMvc.perform(get(ReverseRouter.route(
        on(UpcomingTendersController.class).handleDownload(PROJECT_ID, ProjectFileTestUtil.FILE_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }
}
