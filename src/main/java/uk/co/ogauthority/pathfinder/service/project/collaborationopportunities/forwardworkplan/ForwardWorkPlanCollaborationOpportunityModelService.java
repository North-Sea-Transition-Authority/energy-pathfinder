package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@Service
public class ForwardWorkPlanCollaborationOpportunityModelService {

  public static final String PAGE_NAME = "Collaboration opportunities";

  public ModelAndView getViewCollaborationOpportunitiesModelAndView() {
    return new ModelAndView(
        "project/collaborationopportunities/forwardworkplan/forwardWorkPlanCollaborationOpportunitiesFormSummary"
    )
        .addObject("pageHeading", PAGE_NAME);
  }
}
