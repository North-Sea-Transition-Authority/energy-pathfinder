package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import javax.validation.Validation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.CollaborationOpportunitiesRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.CollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class CollaborationOpportunityServiceValidationTest {

  @Mock
  private CollaborationOpportunitiesRepository collaborationOpportunitiesRepository;

  @Mock
  private CollaborationOpportunityFormValidator collaborationOpportunityFormValidator;

  @Mock
  private SearchSelectorService searchSelectorService;

  @Mock
  private FunctionService functionService;

  @Mock
  private CollaborationOpportunityFileLinkService collaborationOpportunityFileLinkService;

  @Mock
  private ProjectDetailFileService projectDetailFileService;

  @Mock
  private ProjectSetupService projectSetupService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  private CollaborationOpportunitiesService collaborationOpportunitiesService;

  private final ProjectDetail details = ProjectUtil.getProjectDetails();


  @Before
  public void setUp() {
    var validator = new SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator());
    var validationService = new ValidationService(validator);
    collaborationOpportunitiesService = new CollaborationOpportunitiesService(
        searchSelectorService,
        functionService,
        validationService,
        collaborationOpportunityFormValidator,
        collaborationOpportunitiesRepository,
        collaborationOpportunityFileLinkService,
        projectDetailFileService,
        projectSetupService,
        entityDuplicationService
    );
  }

  @Test
  public void isCompleted_fullForm() {
    var opportunity = CollaborationOpportunityTestUtil.getCollaborationOpportunity(details);
    when(collaborationOpportunitiesRepository.findAllByProjectDetailOrderByIdAsc(any()))
        .thenReturn(Collections.singletonList(opportunity));
    assertThat(collaborationOpportunitiesService.isComplete(details)).isTrue();
  }

  @Test
  public void isCompleted_incompleteForm() {
    when(collaborationOpportunitiesRepository.findAllByProjectDetailOrderByIdAsc(any())).thenReturn(Collections.emptyList());
    assertThat(collaborationOpportunitiesService.isComplete(details)).isFalse();
  }

  @Test
  public void isValid_fullForm() {
    var opportunity = CollaborationOpportunityTestUtil.getCollaborationOpportunity(details);
    assertThat(collaborationOpportunitiesService.isValid(opportunity, ValidationType.FULL)).isTrue();
  }

  @Test
  public void isValid_incompleteForm() {
    var opportunity = CollaborationOpportunityTestUtil.getCollaborationOpportunity(details);
    opportunity.setJobTitle(null);
    assertThat(collaborationOpportunitiesService.isValid(opportunity, ValidationType.FULL)).isFalse();
  }
}
