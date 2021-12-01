package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.validation.Validation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationSetup;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanCollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanCollaborationOpportunityServiceValidationTest {

  @Mock
  private ProjectSetupService projectSetupService;

  @Mock
  private ProjectDetailFileService projectDetailFileService;

  @Mock
  private ForwardWorkPlanCollaborationOpportunityRepository forwardWorkPlanCollaborationOpportunityRepository;

  @Mock
  private ForwardWorkPlanCollaborationOpportunityFileLinkService forwardWorkPlanCollaborationOpportunityFileLinkService;

  @Mock
  private ForwardWorkPlanCollaborationOpportunityFormValidator forwardWorkPlanCollaborationOpportunityFormValidator;

  @Mock
  private ForwardWorkPlanCollaborationSetupService forwardWorkPlanCollaborationSetupService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  @Mock
  private SearchSelectorService searchSelectorService;

  @Mock
  private FunctionService functionService;

  private ProjectDetail projectDetail;

  private ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService;

  @Before
  public void setUp() {

    final var validator = new SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator());
    final var validationService = new ValidationService(validator);

    forwardWorkPlanCollaborationOpportunityService = new ForwardWorkPlanCollaborationOpportunityService(
        searchSelectorService,
        functionService,
        projectSetupService,
        projectDetailFileService,
        forwardWorkPlanCollaborationOpportunityRepository,
        forwardWorkPlanCollaborationOpportunityFileLinkService,
        forwardWorkPlanCollaborationOpportunityFormValidator,
        forwardWorkPlanCollaborationSetupService,
        validationService,
        entityDuplicationService
    );

    projectDetail = ProjectUtil.getProjectDetails();
  }

  @Test
  public void isComplete_whenNoSetupAnswer_thenFalse() {

    final var collaborationSetup = new ForwardWorkPlanCollaborationSetup();
    collaborationSetup.setHasOtherCollaborationToAdd(null);

    when(forwardWorkPlanCollaborationSetupService.getCollaborationSetupFromDetail(projectDetail))
        .thenReturn(Optional.of(collaborationSetup));

    final var isComplete = forwardWorkPlanCollaborationOpportunityService.isComplete(projectDetail);

    assertThat(isComplete).isFalse();
  }

  @Test
  public void isComplete_whenCollaborationsToAddAndNoneAdded_thenFalse() {

    final var collaborationSetup = new ForwardWorkPlanCollaborationSetup();
    collaborationSetup.setHasCollaborationToAdd(true);
    collaborationSetup.setHasOtherCollaborationToAdd(false);

    when(forwardWorkPlanCollaborationSetupService.getCollaborationSetupFromDetail(projectDetail))
        .thenReturn(Optional.of(collaborationSetup));

    when(forwardWorkPlanCollaborationOpportunityRepository.findAllByProjectDetailOrderByIdAsc(projectDetail))
        .thenReturn(Collections.emptyList());

    final var isComplete = forwardWorkPlanCollaborationOpportunityService.isComplete(projectDetail);

    assertThat(isComplete).isFalse();

  }

  @Test
  public void isComplete_whenCollaborationsToAddAndInvalidCollaborationAdded_thenFalse() {

    final var collaborationSetup = new ForwardWorkPlanCollaborationSetup();
    collaborationSetup.setHasCollaborationToAdd(true);
    collaborationSetup.setHasOtherCollaborationToAdd(false);

    when(forwardWorkPlanCollaborationSetupService.getCollaborationSetupFromDetail(projectDetail))
        .thenReturn(Optional.of(collaborationSetup));

    final var invalidCollaborationOpportunity = new ForwardWorkPlanCollaborationOpportunity();

    when(forwardWorkPlanCollaborationOpportunityRepository.findAllByProjectDetailOrderByIdAsc(projectDetail))
        .thenReturn(List.of(invalidCollaborationOpportunity));

    final var isComplete = forwardWorkPlanCollaborationOpportunityService.isComplete(projectDetail);

    assertThat(isComplete).isFalse();

  }

  @Test
  public void isComplete_whenHasNoCollaborationsToAdd_thenTrue() {

    final var collaborationSetup = new ForwardWorkPlanCollaborationSetup();
    collaborationSetup.setHasCollaborationToAdd(false);

    when(forwardWorkPlanCollaborationSetupService.getCollaborationSetupFromDetail(projectDetail))
        .thenReturn(Optional.of(collaborationSetup));

    final var isComplete = forwardWorkPlanCollaborationOpportunityService.isComplete(projectDetail);

    assertThat(isComplete).isTrue();

  }

  @Test
  public void isComplete_whenCollaborationsToAddAndValidCollaborationAddedAndNoMoreToAdd_thenTrue() {

    final var collaborationSetup = new ForwardWorkPlanCollaborationSetup();
    collaborationSetup.setHasCollaborationToAdd(true);
    collaborationSetup.setHasOtherCollaborationToAdd(false);

    when(forwardWorkPlanCollaborationSetupService.getCollaborationSetupFromDetail(projectDetail))
        .thenReturn(Optional.of(collaborationSetup));

    final var validCollaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(projectDetail);

    when(forwardWorkPlanCollaborationOpportunityRepository.findAllByProjectDetailOrderByIdAsc(projectDetail))
        .thenReturn(List.of(validCollaborationOpportunity));

    final var isComplete = forwardWorkPlanCollaborationOpportunityService.isComplete(projectDetail);

    assertThat(isComplete).isTrue();

  }

  @Test
  public void isComplete_whenCollaborationsToAddAndAndMoreToAdd_thenFalse() {

    final var collaborationSetup = new ForwardWorkPlanCollaborationSetup();
    collaborationSetup.setHasCollaborationToAdd(true);
    collaborationSetup.setHasOtherCollaborationToAdd(true);

    when(forwardWorkPlanCollaborationSetupService.getCollaborationSetupFromDetail(projectDetail))
        .thenReturn(Optional.of(collaborationSetup));

    final var isComplete = forwardWorkPlanCollaborationOpportunityService.isComplete(projectDetail);

    assertThat(isComplete).isFalse();

  }

  @Test
  public void isValid_whenInvalid_thenFalse() {

    final var invalidCollaborationOpportunity = new ForwardWorkPlanCollaborationOpportunity();

    final var isValid = forwardWorkPlanCollaborationOpportunityService.isValid(
        invalidCollaborationOpportunity,
        ValidationType.FULL
    );

    assertThat(isValid).isFalse();
  }

  @Test
  public void isValid_whenValid_thenTrue() {

    final var validCollaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(projectDetail);

    final var isValid = forwardWorkPlanCollaborationOpportunityService.isValid(
        validCollaborationOpportunity,
        ValidationType.FULL
    );

    assertThat(isValid).isTrue();

  }
}
