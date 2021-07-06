package uk.co.ogauthority.pathfinder.service.project.campaigninformation;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.SelectableProject;
import uk.co.ogauthority.pathfinder.model.view.campaigninformation.CampaignProjectView;

@Service
public class CampaignProjectValidatorService {

  protected static final String NON_PUBLISHED_PROJECT_ERROR_MESSAGE = "%s is not a published %s and must be removed";
  protected static final String CURRENT_PROJECT_IN_CAMPAIGN_ERROR_MESSAGE = "%s must not be included as part of its own campaign";

  private final CampaignProjectRestService campaignProjectRestService;

  @Autowired
  public CampaignProjectValidatorService(CampaignProjectRestService campaignProjectRestService) {
    this.campaignProjectRestService = campaignProjectRestService;
  }

  public void validateCampaignProjects(Project currentProject,
                                       List<Integer> campaignProjectIdsFromForm,
                                       Errors formErrors) {
    final var selectedProjects = campaignProjectRestService.getSelectableProjectsByIdIn(campaignProjectIdsFromForm);

    addErrorsToNonPublishedProjects(selectedProjects, formErrors);
    addErrorIfCurrentProjectIsIncluded(currentProject, selectedProjects, formErrors);
  }

  private void addErrorsToNonPublishedProjects(List<SelectableProject> selectedProjects,
                                              Errors formErrors) {
    selectedProjects
        .stream()
        .filter(selectedProject -> !selectedProject.isPublished())
        .map(CampaignProjectView::new)
        .forEach(campaignProjectView -> formErrors.reject(
            String.format("%s-text", campaignProjectView.getSelectionId()),
            String.format(
                NON_PUBLISHED_PROJECT_ERROR_MESSAGE,
                campaignProjectView.getSelectableProject().getProjectDisplayName(),
                campaignProjectView.getSelectableProject().getProjectType().getLowercaseDisplayName()
            )
        ));
  }

  private void addErrorIfCurrentProjectIsIncluded(Project currentProject,
                                                  List<SelectableProject> selectedProjects,
                                                  Errors formErrors) {
    final var currentCampaignProject = selectedProjects
        .stream()
        .filter(selectableProject -> selectableProject.getProjectId().equals(currentProject.getId()))
        .map(CampaignProjectView::new)
        .findFirst();

    currentCampaignProject.ifPresent(campaignProjectView ->
        formErrors.reject(
            String.format("%s-text", campaignProjectView.getSelectionId()),
            String.format(
                CURRENT_PROJECT_IN_CAMPAIGN_ERROR_MESSAGE,
                campaignProjectView.getSelectableProject().getProjectDisplayName()
            )
        )
    );
  }
}
