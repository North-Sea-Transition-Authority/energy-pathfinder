package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationSetup;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationSetupForm;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan.ForwardWorkPlanCollaborationSetupViewUtil;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationSetupRepository;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanCollaborationSetupServiceTest {

  @Mock
  private ForwardWorkPlanCollaborationSetupRepository forwardWorkPlanCollaborationSetupRepository;

  @Mock
  private ValidationService validationService;

  private ProjectDetail projectDetail;

  private ForwardWorkPlanCollaborationSetupService forwardWorkPlanCollaborationSetupService;

  @Before
  public void setup() {
    forwardWorkPlanCollaborationSetupService = new ForwardWorkPlanCollaborationSetupService(
        forwardWorkPlanCollaborationSetupRepository,
        validationService
    );

    projectDetail = ProjectUtil.getProjectDetails();
  }

  @Test
  public void getCollaborationSetupFormFromDetail_whenEntityExists_thenPopulatedForm() {

    final var collaborationSetupEntity = new ForwardWorkPlanCollaborationSetup();
    collaborationSetupEntity.setHasCollaborationToAdd(true);

    when(forwardWorkPlanCollaborationSetupRepository.findByProjectDetail(projectDetail))
        .thenReturn(Optional.of(collaborationSetupEntity));

    final var resultingForm = forwardWorkPlanCollaborationSetupService.getCollaborationSetupFormFromDetail(projectDetail);

    assertThat(resultingForm.getHasCollaborationsToAdd()).isEqualTo(collaborationSetupEntity.getHasCollaborationToAdd());
  }

  @Test
  public void getCollaborationSetupFormFromDetail_whenNoEntityExists_thenEmptyForm() {

    when(forwardWorkPlanCollaborationSetupRepository.findByProjectDetail(projectDetail))
        .thenReturn(Optional.empty());

    final var resultingForm = forwardWorkPlanCollaborationSetupService.getCollaborationSetupFormFromDetail(projectDetail);

    assertThat(resultingForm.getHasCollaborationsToAdd()).isNull();
  }

  @Test
  public void getCollaborationSetupFromDetail_whenExists_thenPopulatedOptional() {

    final var collaborationSetupEntity = new ForwardWorkPlanCollaborationSetup();

    when(forwardWorkPlanCollaborationSetupRepository.findByProjectDetail(projectDetail))
        .thenReturn(Optional.of(collaborationSetupEntity));

    final var resultingEntity = forwardWorkPlanCollaborationSetupService.getCollaborationSetupFromDetail(
        projectDetail
    );

    assertThat(resultingEntity).isEqualTo(Optional.of(collaborationSetupEntity));

  }

  @Test
  public void getCollaborationSetupFromDetail_whenNotExists_thenEmptyOptional() {

    when(forwardWorkPlanCollaborationSetupRepository.findByProjectDetail(projectDetail))
        .thenReturn(Optional.empty());

    final var resultingEntity = forwardWorkPlanCollaborationSetupService.getCollaborationSetupFromDetail(
        projectDetail
    );

    assertThat(resultingEntity).isEmpty();
  }

  @Test
  public void validate_verifyInteractions() {

    final var form = new ForwardWorkPlanCollaborationSetupForm();
    final var bindingResult = new BeanPropertyBindingResult(form, "form");
    final var validationType = ValidationType.FULL;

    forwardWorkPlanCollaborationSetupService.validate(form, bindingResult, validationType);

    verify(validationService, times(1)).validate(
        form,
        bindingResult,
        validationType
    );
  }

  @Test
  public void saveForwardWorkPlanCollaborationSetup_verifyInteractions() {

    final var collaborationSetupForm = new ForwardWorkPlanCollaborationSetupForm();
    collaborationSetupForm.setHasCollaborationsToAdd(true);

    final var expectedCollaborationSetupEntity = new ForwardWorkPlanCollaborationSetup();
    expectedCollaborationSetupEntity.setHasCollaborationToAdd(collaborationSetupForm.getHasCollaborationsToAdd());

    when(forwardWorkPlanCollaborationSetupRepository.save(any()))
        .thenAnswer(invocation -> invocation.getArguments()[0]);

    final var persistedEntity = forwardWorkPlanCollaborationSetupService.saveForwardWorkPlanCollaborationSetup(
        collaborationSetupForm,
        projectDetail
    );

    assertThat(persistedEntity).isEqualTo(expectedCollaborationSetupEntity);

    verify(forwardWorkPlanCollaborationSetupRepository, times(1)).save(
        expectedCollaborationSetupEntity
    );
  }

  @Test
  public void persistForwardWorkPlanCollaborationSetup_verifyInteractions() {

    final var entityToPersist = new ForwardWorkPlanCollaborationSetup();

    forwardWorkPlanCollaborationSetupService.persistForwardWorkPlanCollaborationSetup(entityToPersist);

    verify(forwardWorkPlanCollaborationSetupRepository, times(1)).save(entityToPersist);
  }

  @Test
  public void getCollaborationSetupView_withProjectDetailVariant_whenCollaborationSetupFound_thenPopulatedViewReturned() {

    final var populatedSetupEntity = new ForwardWorkPlanCollaborationSetup();
    populatedSetupEntity.setHasCollaborationToAdd(true);

    when(forwardWorkPlanCollaborationSetupRepository.findByProjectDetail(projectDetail))
        .thenReturn(Optional.of(populatedSetupEntity));

    final var expectedSetupView = ForwardWorkPlanCollaborationSetupViewUtil.from(populatedSetupEntity);

    final var resultingSetupView = forwardWorkPlanCollaborationSetupService.getCollaborationSetupView(projectDetail);

    assertThat(resultingSetupView).isEqualTo(expectedSetupView);
  }

  @Test
  public void getCollaborationSetupView_withProjectDetailVariant_whenCollaborationSetupNotFound_thenEmptyViewReturned() {

    when(forwardWorkPlanCollaborationSetupRepository.findByProjectDetail(projectDetail))
        .thenReturn(Optional.empty());

    final var emptySetupEntity = new ForwardWorkPlanCollaborationSetup();
    final var expectedSetupView = ForwardWorkPlanCollaborationSetupViewUtil.from(emptySetupEntity);

    final var resultingSetupView = forwardWorkPlanCollaborationSetupService.getCollaborationSetupView(projectDetail);

    assertThat(resultingSetupView).isEqualTo(expectedSetupView);
  }

  @Test
  public void getCollaborationSetupView_withProjectAndVersionVariant_whenCollaborationSetupFound_thenPopulatedViewReturned() {

    final var project = projectDetail.getProject();
    final var version = projectDetail.getVersion();

    final var populatedSetupEntity = new ForwardWorkPlanCollaborationSetup();
    populatedSetupEntity.setHasCollaborationToAdd(true);

    when(forwardWorkPlanCollaborationSetupRepository.findByProjectDetail_ProjectAndProjectDetail_Version(
        project,
        version
    )).thenReturn(Optional.of(populatedSetupEntity));

    final var expectedSetupView = ForwardWorkPlanCollaborationSetupViewUtil.from(populatedSetupEntity);

    final var resultingSetupView = forwardWorkPlanCollaborationSetupService.getCollaborationSetupView(
        project,
        version
    );

    assertThat(resultingSetupView).isEqualTo(expectedSetupView);
  }

  @Test
  public void getCollaborationSetupView_withProjectAndVersionVariant_whenCollaborationSetupNotFound_thenEmptyViewReturned() {

    final var project = projectDetail.getProject();
    final var version = projectDetail.getVersion();

    when(forwardWorkPlanCollaborationSetupRepository.findByProjectDetail_ProjectAndProjectDetail_Version(
        project,
        version
    )).thenReturn(Optional.empty());

    final var emptySetupEntity = new ForwardWorkPlanCollaborationSetup();
    final var expectedSetupView = ForwardWorkPlanCollaborationSetupViewUtil.from(emptySetupEntity);

    final var resultingSetupView = forwardWorkPlanCollaborationSetupService.getCollaborationSetupView(
        project,
        version
    );

    assertThat(resultingSetupView).isEqualTo(expectedSetupView);
  }
}