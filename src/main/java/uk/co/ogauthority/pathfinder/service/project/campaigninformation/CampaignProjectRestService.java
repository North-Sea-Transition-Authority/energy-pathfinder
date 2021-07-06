package uk.co.ogauthority.pathfinder.service.project.campaigninformation;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.rest.CampaignProjectRestController;
import uk.co.ogauthority.pathfinder.model.entity.project.SelectableProject;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.view.campaigninformation.CampaignProjectView;
import uk.co.ogauthority.pathfinder.service.project.SelectableProjectService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@Service
public class CampaignProjectRestService {

  private final SearchSelectorService searchSelectorService;
  private final SelectableProjectService selectableProjectService;

  @Autowired
  public CampaignProjectRestService(SearchSelectorService searchSelectorService,
                                    SelectableProjectService selectableProjectService) {
    this.searchSelectorService = searchSelectorService;
    this.selectableProjectService = selectableProjectService;
  }

  public List<RestSearchItem> searchProjectsWithDisplayNameOrOperatorGroupNameContaining(String searchTerm,
                                                                                         ProjectType projectType) {

    final var selectableCampaignProjects = selectableProjectService.getPublishedSelectableProjects(
        searchTerm,
        projectType
    )
        .stream()
        .map(CampaignProjectView::new)
        .collect(Collectors.toList());

    return searchSelectorService.search(
        searchTerm,
        selectableCampaignProjects
    );
  }

  protected static String getCampaignProjectRestUrl() {
    return SearchSelectorService.route(on(CampaignProjectRestController.class)
        .searchCampaignableInfrastructureProjects(null));
  }

  protected List<SelectableProject> getSelectableProjectsByIdIn(List<Integer> campaignProjectIds) {
    return selectableProjectService.getSelectableProjectsByIdIn(campaignProjectIds);
  }
}
