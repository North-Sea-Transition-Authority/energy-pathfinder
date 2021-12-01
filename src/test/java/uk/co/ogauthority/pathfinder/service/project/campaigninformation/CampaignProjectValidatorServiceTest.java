package uk.co.ogauthority.pathfinder.service.project.campaigninformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.SelectableProject;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.campaigninformation.CampaignInformationForm;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class CampaignProjectValidatorServiceTest {

  @Mock
  private CampaignProjectRestService campaignProjectRestService;

  private final Project project = ProjectUtil.getProject();

  private CampaignProjectValidatorService campaignProjectValidatorService;

  @Before
  public void setup() {
    campaignProjectValidatorService = new CampaignProjectValidatorService(campaignProjectRestService);

    // set to a large number so to not hit current project selected validation as part of other tests
    project.setId(1000);
  }

  @Test
  public void validateCampaignProjects_whenAllProjectsPublished_thenNoErrors() {

    final var projectIds = List.of(1);

    final var selectableProject1 = createSelectableProject();
    selectableProject1.setPublished(true);

    final var errors = new BeanPropertyBindingResult(CampaignInformationForm.class, "form");

    when(campaignProjectRestService.getSelectableProjectsByIdIn(projectIds)).thenReturn(List.of(selectableProject1));

    campaignProjectValidatorService.validateCampaignProjects(project, projectIds, errors);

    assertThat(errors.hasErrors()).isFalse();
  }

  @Test
  public void validateCampaignProjects_whenSomeProjectsNotPublished_thenErrors() {

    final var projectIds = List.of(1, 2);

    final var publishedSelectableProject = createSelectableProject();
    publishedSelectableProject.setPublished(true);
    publishedSelectableProject.setProjectDisplayName("published project");

    final var nonPublishedSelectableProject = createSelectableProject();
    nonPublishedSelectableProject.setPublished(false);
    nonPublishedSelectableProject.setProjectDisplayName("non published project");

    final var errors = new BeanPropertyBindingResult(CampaignInformationForm.class, "form");

    when(campaignProjectRestService.getSelectableProjectsByIdIn(projectIds)).thenReturn(List.of(
        publishedSelectableProject, nonPublishedSelectableProject
    ));

    campaignProjectValidatorService.validateCampaignProjects(project, projectIds, errors);

    assertThat(errors.getGlobalErrors()).extracting(ObjectError::getDefaultMessage).containsExactly(
        getNonPublishedProjectErrorMessage(nonPublishedSelectableProject)
    );
  }

  @Test
  public void validateCampaignProjects_whenAllProjectsNotPublished_thenErrors() {

    final var projectIds = List.of(1, 2);

    final var nonPublishedSelectableProject1 = createSelectableProject();
    nonPublishedSelectableProject1.setPublished(false);
    nonPublishedSelectableProject1.setProjectDisplayName("non published project 1");

    final var nonPublishedSelectableProject2 = createSelectableProject();
    nonPublishedSelectableProject2.setPublished(false);
    nonPublishedSelectableProject2.setProjectDisplayName("non published project 2");

    final var errors = new BeanPropertyBindingResult(CampaignInformationForm.class, "form");

    when(campaignProjectRestService.getSelectableProjectsByIdIn(projectIds)).thenReturn(List.of(
        nonPublishedSelectableProject1, nonPublishedSelectableProject2
    ));

    campaignProjectValidatorService.validateCampaignProjects(project, projectIds, errors);

    assertThat(errors.getGlobalErrors()).extracting(ObjectError::getDefaultMessage).containsExactly(
        getNonPublishedProjectErrorMessage(nonPublishedSelectableProject1),
        getNonPublishedProjectErrorMessage(nonPublishedSelectableProject2)
    );
  }

  @Test
  public void validateCampaignProjects_whenProjectListContainsCurrentProject_thenError() {

    final var currentProject = new Project();
    currentProject.setId(1);

    final var currentProjectIdInList = List.of(currentProject.getId());

    final var currentProjectSelected = createSelectableProject();
    currentProjectSelected.setProjectId(currentProject.getId());

    when(campaignProjectRestService.getSelectableProjectsByIdIn(currentProjectIdInList)).thenReturn(List.of(
        currentProjectSelected
    ));

    final var errors = new BeanPropertyBindingResult(CampaignInformationForm.class, "form");

    campaignProjectValidatorService.validateCampaignProjects(currentProject, currentProjectIdInList, errors);

    assertThat(errors.getGlobalErrors()).extracting(ObjectError::getDefaultMessage).containsExactly(
        String.format(
            CampaignProjectValidatorService.CURRENT_PROJECT_IN_CAMPAIGN_ERROR_MESSAGE,
            currentProjectSelected.getProjectDisplayName()
        )
    );

  }

  @Test
  public void validateCampaignProjects_whenProjectListNotContainCurrentProject_thenNoError() {

    final var currentProject = new Project();
    currentProject.setId(1);

    final var nonCurrentProjectId = 2;

    final var projectIdsWithoutCurrentProject = List.of(nonCurrentProjectId);

    final var randomSelectableProject = createSelectableProject();
    randomSelectableProject.setProjectId(nonCurrentProjectId);

    when(campaignProjectRestService.getSelectableProjectsByIdIn(projectIdsWithoutCurrentProject)).thenReturn(List.of(
        randomSelectableProject
    ));

    final var errors = new BeanPropertyBindingResult(CampaignInformationForm.class, "form");

    campaignProjectValidatorService.validateCampaignProjects(currentProject, projectIdsWithoutCurrentProject, errors);

    assertThat(errors.hasErrors()).isFalse();
  }

  private String getNonPublishedProjectErrorMessage(SelectableProject selectableProjectInError) {
    return String.format(
        CampaignProjectValidatorService.NON_PUBLISHED_PROJECT_ERROR_MESSAGE,
        selectableProjectInError.getProjectDisplayName(),
        selectableProjectInError.getProjectType().getLowercaseDisplayName()
    );
  }

  private SelectableProject createSelectableProject() {
    final var selectableProject = new SelectableProject();
    selectableProject.setProjectId(1);
    selectableProject.setProjectType(ProjectType.INFRASTRUCTURE);
    selectableProject.setProjectDisplayName("display name");
    selectableProject.setOperatorGroupName("operator name");
    selectableProject.setPublished(true);
    return selectableProject;
  }
}