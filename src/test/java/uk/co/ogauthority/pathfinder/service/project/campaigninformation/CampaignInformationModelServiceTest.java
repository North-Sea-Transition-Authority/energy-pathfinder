package uk.co.ogauthority.pathfinder.service.project.campaigninformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.project.campaigninformation.CampaignInformationController;
import uk.co.ogauthority.pathfinder.model.entity.project.PublishedProjectView;
import uk.co.ogauthority.pathfinder.model.form.project.campaigninformation.CampaignInformationForm;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectTypeModelUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class CampaignInformationModelServiceTest {

  @Mock
  private BreadcrumbService breadcrumbService;

  @Mock
  private CampaignProjectService campaignProjectService;

  private CampaignInformationModelService campaignInformationModelService;

  @Before
  public void setup() {
    campaignInformationModelService = new CampaignInformationModelService(
        breadcrumbService,
        campaignProjectService
    );
  }

  @Test
  public void getCampaignInformationModelAndView_assertModelProperties() {

    final var campaignInformationForm = new CampaignInformationForm();
    final var projectDetail = ProjectUtil.getProjectDetails();

    final var publishedProjectRestUrl = "/example/url";
    when(campaignProjectService.getPublishedProjectRestUrl()).thenReturn(publishedProjectRestUrl);

    final var expectedProjectViews = List.of(new PublishedProjectView());
    when(campaignProjectService.getPublishedProjectViews(campaignInformationForm)).thenReturn(expectedProjectViews);

    final var resultingModel = campaignInformationModelService.getCampaignInformationModelAndView(
        projectDetail,
        campaignInformationForm
    );
    assertThat(resultingModel.getViewName()).isEqualTo(CampaignInformationModelService.FORM_TEMPLATE_PATH);
    assertThat(resultingModel.getModelMap()).containsExactly(
        entry("pageTitle", CampaignInformationController.PAGE_NAME),
        entry("form", campaignInformationForm),
        entry("publishedProjectRestUrl", publishedProjectRestUrl),
        entry("alreadyAddedProjects", expectedProjectViews),
        entry(
            ProjectTypeModelUtil.PROJECT_TYPE_DISPLAY_NAME_MODEL_ATTR,
            projectDetail.getProjectType().getDisplayName()
        ),
        entry(
            ProjectTypeModelUtil.PROJECT_TYPE_LOWERCASE_DISPLAY_NAME_MODEL_ATTR,
            projectDetail.getProjectType().getLowercaseDisplayName()
        )
    );

    verify(breadcrumbService, times(1)).fromTaskList(
        projectDetail.getProject().getId(),
        resultingModel,
        CampaignInformationController.PAGE_NAME
    );
  }

}