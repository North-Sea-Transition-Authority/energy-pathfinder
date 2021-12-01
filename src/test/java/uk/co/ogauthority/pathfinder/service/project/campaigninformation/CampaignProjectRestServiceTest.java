package uk.co.ogauthority.pathfinder.service.project.campaigninformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.SelectableProject;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.view.campaigninformation.CampaignProjectView;
import uk.co.ogauthority.pathfinder.service.project.SelectableProjectService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@RunWith(MockitoJUnitRunner.class)
public class CampaignProjectRestServiceTest {

  private static final String SEARCH_TERM = "search term";
  private static final ProjectType PROJECT_TYPE = ProjectType.INFRASTRUCTURE;

  @Mock
  private SearchSelectorService searchSelectorService;

  @Mock
  private SelectableProjectService selectableProjectService;

  private CampaignProjectRestService campaignProjectRestService;

  @Before
  public void setup() {
    campaignProjectRestService = new CampaignProjectRestService(
        searchSelectorService,
        selectableProjectService
    );
  }

  @Test
  public void searchProjectsWithDisplayNameOrOperatorGroupNameContaining_verifyInteractions() {

    final var selectableProject = new SelectableProject();
    final var campaignProjectView = new CampaignProjectView(selectableProject);

    final var selectableProjectList = List.of(selectableProject);
    final var campaignProjectList = List.of(campaignProjectView);

    when(selectableProjectService.getPublishedSelectableProjects(
        SEARCH_TERM,
        PROJECT_TYPE
    )).thenReturn(selectableProjectList);

    campaignProjectRestService.searchProjectsWithDisplayNameOrOperatorGroupNameContaining(
        SEARCH_TERM,
        PROJECT_TYPE
    );

    verify(searchSelectorService, times(1)).search(SEARCH_TERM, campaignProjectList);
  }

  @Test
  public void searchProjectsWithDisplayNameOrOperatorGroupNameContaining_whenNoResults_thenEmptyListReturned() {

    when(searchSelectorService.search(eq(SEARCH_TERM), any())).thenReturn(Collections.emptyList());

    final var resultingRestSearchItems = campaignProjectRestService.searchProjectsWithDisplayNameOrOperatorGroupNameContaining(
        SEARCH_TERM,
        PROJECT_TYPE
    );

    assertThat(resultingRestSearchItems).isEmpty();
  }

  @Test
  public void searchProjectsWithDisplayNameOrOperatorGroupNameContaining_whenResults_thenPopulatedListReturned() {

    final var expectedRestSearchItems = List.of(new RestSearchItem("1", "value"));

    when(searchSelectorService.search(eq(SEARCH_TERM), any())).thenReturn(expectedRestSearchItems);

    final var resultingRestSearchItems = campaignProjectRestService.searchProjectsWithDisplayNameOrOperatorGroupNameContaining(
        SEARCH_TERM,
        PROJECT_TYPE
    );

    assertThat(resultingRestSearchItems).isEqualTo(expectedRestSearchItems);
  }
}