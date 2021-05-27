package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanCollaborationOpportunityModelServiceTest {

  private ForwardWorkPlanCollaborationOpportunityModelService forwardWorkPlanCollaborationOpportunityModelService;

  @Before
  public void setup() {
    forwardWorkPlanCollaborationOpportunityModelService = new ForwardWorkPlanCollaborationOpportunityModelService();
  }

  @Test
  public void getViewCollaborationOpportunitiesModelAndView_assertModelProperties() {

    final var modelAndView = forwardWorkPlanCollaborationOpportunityModelService.getViewCollaborationOpportunitiesModelAndView();

    assertThat(modelAndView.getModelMap()).containsExactly(
        entry("pageHeading", ForwardWorkPlanCollaborationOpportunityModelService.PAGE_NAME)
    );
  }

}