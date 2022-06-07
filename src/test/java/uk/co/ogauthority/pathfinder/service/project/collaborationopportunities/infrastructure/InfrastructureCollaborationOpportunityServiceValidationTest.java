package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.infrastructure;

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
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunitiesRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.InfrastructureCollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class InfrastructureCollaborationOpportunityServiceValidationTest {

  @Mock
  private InfrastructureCollaborationOpportunitiesRepository infrastructureCollaborationOpportunitiesRepository;

  @Mock
  private InfrastructureCollaborationOpportunityFormValidator infrastructureCollaborationOpportunityFormValidator;

  @Mock
  private SearchSelectorService searchSelectorService;

  @Mock
  private FunctionService functionService;

  @Mock
  private InfrastructureCollaborationOpportunityFileLinkService infrastructureCollaborationOpportunityFileLinkService;

  @Mock
  private ProjectDetailFileService projectDetailFileService;

  @Mock
  private ProjectSetupService projectSetupService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  @Mock
  private TeamService teamService;

  private InfrastructureCollaborationOpportunitiesService infrastructureCollaborationOpportunitiesService;

  private final ProjectDetail details = ProjectUtil.getProjectDetails();


  @Before
  public void setUp() {
    var validator = new SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator());
    var validationService = new ValidationService(validator);
    infrastructureCollaborationOpportunitiesService = new InfrastructureCollaborationOpportunitiesService(
        searchSelectorService,
        functionService,
        validationService,
        infrastructureCollaborationOpportunityFormValidator,
        infrastructureCollaborationOpportunitiesRepository,
        infrastructureCollaborationOpportunityFileLinkService,
        projectDetailFileService,
        projectSetupService,
        entityDuplicationService,
        teamService);
  }

  @Test
  public void isCompleted_fullForm() {
    var opportunity = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(details);
    when(infrastructureCollaborationOpportunitiesRepository.findAllByProjectDetailOrderByIdAsc(any()))
        .thenReturn(Collections.singletonList(opportunity));
    assertThat(infrastructureCollaborationOpportunitiesService.isComplete(details)).isTrue();
  }

  @Test
  public void isCompleted_incompleteForm() {
    when(infrastructureCollaborationOpportunitiesRepository.findAllByProjectDetailOrderByIdAsc(any())).thenReturn(Collections.emptyList());
    assertThat(infrastructureCollaborationOpportunitiesService.isComplete(details)).isFalse();
  }

  @Test
  public void isValid_fullForm() {
    var opportunity = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(details);
    assertThat(infrastructureCollaborationOpportunitiesService.isValid(opportunity, ValidationType.FULL)).isTrue();
  }

  @Test
  public void isValid_incompleteForm() {
    var opportunity = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(details);
    opportunity.setJobTitle(null);
    assertThat(infrastructureCollaborationOpportunitiesService.isValid(opportunity, ValidationType.FULL)).isFalse();
  }
}
