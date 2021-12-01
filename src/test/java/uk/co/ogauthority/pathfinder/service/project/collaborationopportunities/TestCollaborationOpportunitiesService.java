package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunityCommon;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityFormCommon;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.TestCollaborationOpportunityCommon;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@Service
public class TestCollaborationOpportunitiesService extends CollaborationOpportunitiesService {

  public TestCollaborationOpportunitiesService(
      SearchSelectorService searchSelectorService,
      FunctionService functionService,
      ProjectSetupService projectSetupService,
      ProjectDetailFileService projectDetailFileService) {
    super(
        searchSelectorService,
        functionService,
        projectSetupService,
        projectDetailFileService
    );
  }

  @Override
  public List<? extends CollaborationOpportunityCommon> getOpportunitiesForDetail(ProjectDetail projectDetail) {
    return List.of(new TestCollaborationOpportunityCommon());
  }

  @Override
  public <E extends CollaborationOpportunityCommon> CollaborationOpportunityFormCommon getForm(E entity) {
    return new TestCollaborationOpportunityForm();
  }

  @Override
  public <F extends CollaborationOpportunityFormCommon> BindingResult validate(F form,
                                                                               BindingResult bindingResult,
                                                                               ValidationType validationType) {
    return bindingResult;
  }
}
