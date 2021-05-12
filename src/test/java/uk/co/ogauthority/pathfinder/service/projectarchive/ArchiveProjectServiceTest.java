package uk.co.ogauthority.pathfinder.service.projectarchive;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.projectarchive.ArchiveProjectController;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectarchive.ProjectArchiveDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.projectarchive.ArchiveProjectForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.projectarchive.ProjectArchiveDetailRepository;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.CancelDraftProjectVersionService;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectHeaderSummaryService;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ArchiveProjectServiceTest {

  @Mock
  private ProjectArchiveDetailRepository projectArchiveDetailRepository;

  @Mock
  private ProjectUpdateService projectUpdateService;

  @Mock
  private CancelDraftProjectVersionService cancelDraftProjectVersionService;

  @Mock
  private ProjectHeaderSummaryService projectHeaderSummaryService;

  @Mock
  private ValidationService validationService;

  @Mock
  private BreadcrumbService breadcrumbService;

  private ArchiveProjectService archiveProjectService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    archiveProjectService = new ArchiveProjectService(
        projectArchiveDetailRepository,
        projectUpdateService,
        cancelDraftProjectVersionService,
        projectHeaderSummaryService,
        validationService,
        breadcrumbService
    );

    when(projectArchiveDetailRepository.save(any(ProjectArchiveDetail.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void archiveProject_whenNotDraft() {
    var newProjectDetail = ProjectUtil.getProjectDetails();

    when(projectUpdateService.createNewProjectVersion(projectDetail, ProjectStatus.ARCHIVED, authenticatedUser)).thenReturn(
        newProjectDetail
    );

    var form = new ArchiveProjectForm();
    form.setArchiveReason("Test archive reason");

    var projectArchiveDetail = archiveProjectService.archiveProject(projectDetail, authenticatedUser, form);

    verify(cancelDraftProjectVersionService, times(1)).cancelDraftIfExists(projectDetail.getProject().getId());
    verify(projectUpdateService, times(1)).createNewProjectVersion(projectDetail, ProjectStatus.ARCHIVED, authenticatedUser);

    assertThat(projectArchiveDetail.getProjectDetail()).isEqualTo(newProjectDetail);
    assertThat(projectArchiveDetail.getArchiveReason()).isEqualTo(form.getArchiveReason());

    verify(projectArchiveDetailRepository, times(1)).save(projectArchiveDetail);
  }

  @Test
  public void getProjectArchiveDetailOrError_whenFound_thenReturn() {
    var projectArchiveDetail = new ProjectArchiveDetail();

    when(projectArchiveDetailRepository.findByProjectDetail(projectDetail)).thenReturn(Optional.of(projectArchiveDetail));

    assertThat(archiveProjectService.getProjectArchiveDetailOrError(projectDetail)).isEqualTo(projectArchiveDetail);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getProjectArchiveDetailOrError_whenNotFound_thenError() {
    when(projectArchiveDetailRepository.findByProjectDetail(projectDetail)).thenReturn(Optional.empty());

    archiveProjectService.getProjectArchiveDetailOrError(projectDetail);
  }

  @Test
  public void validate() {
    var form = new ArchiveProjectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    archiveProjectService.validate(form, bindingResult);

    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  public void getArchiveProjectModelAndView() {
    var form = new ArchiveProjectForm();
    var projectId = projectDetail.getProject().getId();
    var projectHeaderHtml = "html";

    when(projectHeaderSummaryService.getProjectHeaderHtml(projectDetail, authenticatedUser)).thenReturn(projectHeaderHtml);

    var modelAndView = archiveProjectService.getArchiveProjectModelAndView(
        projectDetail,
        authenticatedUser,
        form
    );

    final var pageHeading = String.format(
        "%s %s",
        ArchiveProjectController.ARCHIVE_PROJECT_PAGE_NAME_PREFIX,
        projectDetail.getProjectType().getLowercaseDisplayName()
    );

    assertThat(modelAndView.getViewName()).isEqualTo(ArchiveProjectService.ARCHIVE_PROJECT_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("projectHeaderHtml", projectHeaderHtml),
        entry("form", form),
        entry("cancelUrl", ReverseRouter.route(on(ManageProjectController.class)
            .getProject(projectId, null, null, null))
        ),
        entry("pageHeading", pageHeading)
    );

    verify(breadcrumbService, times(1)).fromManageProject(
        projectDetail,
        modelAndView,
        pageHeading
    );
  }
}
