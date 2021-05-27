package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.forwardworkplan.ForwardWorkPlanCollaborationOpportunityController;
import uk.co.ogauthority.pathfinder.controller.rest.ForwardWorkPlanCollaborationOpportunityRestController;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanCollaborationOpportunityModelServiceTest {

  @Mock
  private ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService;

  @Mock
  private BreadcrumbService breadcrumbService;

  private ForwardWorkPlanCollaborationOpportunityModelService forwardWorkPlanCollaborationOpportunityModelService;

  @Before
  public void setup() {
    forwardWorkPlanCollaborationOpportunityModelService = new ForwardWorkPlanCollaborationOpportunityModelService(
        forwardWorkPlanCollaborationOpportunityService,
        breadcrumbService
    );
  }

  @Test
  public void getViewCollaborationOpportunitiesModelAndView_assertModelProperties() {

    final var projectId = 1;
    final var modelAndView = forwardWorkPlanCollaborationOpportunityModelService.getViewCollaborationOpportunitiesModelAndView(
        projectId
    );

    assertThat(modelAndView.getModelMap()).containsExactly(
        entry("pageHeading", ForwardWorkPlanCollaborationOpportunityModelService.PAGE_NAME),
        entry(
            "addCollaborationOpportunityFormUrl",
            ReverseRouter.route(on(ForwardWorkPlanCollaborationOpportunityController.class).addCollaborationOpportunity(
                projectId,
                null
            ))
        )
    );
  }

  @Test
  public void getCollaborationOpportunityModelAndView_assertModelProperties() {

    final var form = new ForwardWorkPlanCollaborationOpportunityForm();
    final var preselectedFunction = Function.DRILLING;
    final var preselectedFunctionMap = Map.of(preselectedFunction.getSelectionId(), preselectedFunction.getSelectionText());

    when(forwardWorkPlanCollaborationOpportunityService.getPreSelectedCollaborationFunction(any())).thenReturn(
        preselectedFunctionMap
    );

    final var modelAndView = forwardWorkPlanCollaborationOpportunityModelService.getCollaborationOpportunityModelAndView(
        new ModelAndView(),
        form,
        1
    );

    assertThat(modelAndView.getModelMap()).containsExactly(
        entry("pageHeading", ForwardWorkPlanCollaborationOpportunityModelService.PAGE_NAME_SINGULAR),
        entry(
            "collaborationFunctionRestUrl",
            SearchSelectorService.route(on(ForwardWorkPlanCollaborationOpportunityRestController.class).searchFunctions(null))
        ),
        entry("form", form),
        entry("preselectedFunction", preselectedFunctionMap)
    );

  }

}