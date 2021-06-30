package uk.co.ogauthority.pathfinder.service.project.campaigninformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
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
import org.springframework.validation.FieldError;
import uk.co.ogauthority.pathfinder.controller.project.campaigninformation.CampaignInformationController;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignInformation;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.project.campaigninformation.CampaignInformationForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.project.campaigninformation.CampaignInformationRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.ProjectTypeModelUtil;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.CampaignInformationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class CampaignInformationServiceTest {

  @Mock
  private ProjectSetupService projectSetupService;

  @Mock
  private CampaignInformationRepository campaignInformationRepository;

  @Mock
  private ValidationService validationService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  @Mock
  private BreadcrumbService breadcrumbService;

  private CampaignInformationService campaignInformationService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    campaignInformationService = new CampaignInformationService(
        projectSetupService,
        campaignInformationRepository,
        validationService,
        entityDuplicationService,
        breadcrumbService
    );
    when(campaignInformationRepository.save(any(CampaignInformation.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void createOrUpdateCampaignInformation_whenNoDetailFound_assertPopulatedProperties() {
    when(campaignInformationRepository.findByProjectDetail(projectDetail)).thenReturn(Optional.empty());

    var form = CampaignInformationTestUtil.createCampaignInformationForm();

    var campaignInformation = campaignInformationService.createOrUpdateCampaignInformation(
        form,
        projectDetail
    );

    assertThat(campaignInformation.getProjectDetail()).isEqualTo(projectDetail);
    assertThat(campaignInformation.getScopeDescription()).isEqualTo(form.getScopeDescription());
    assertThat(campaignInformation.isPublishedCampaign()).isEqualTo(form.getPublishedCampaign());
  }

  @Test
  public void createOrUpdateCampaignInformation_whenDetailFound_assertPopulatedProperties() {
    var campaignInformation = CampaignInformationTestUtil.createCampaignInformation();
    when(campaignInformationRepository.findByProjectDetail(projectDetail))
        .thenReturn(Optional.of(campaignInformation));

    var form = new CampaignInformationForm();
    form.setScopeDescription("test");
    form.setPublishedCampaign(true);

    campaignInformation = campaignInformationService.createOrUpdateCampaignInformation(
        form,
        projectDetail
    );

    assertThat(campaignInformation.getProjectDetail()).isEqualTo(projectDetail);
    assertThat(campaignInformation.getScopeDescription()).isEqualTo(form.getScopeDescription());
    assertThat(campaignInformation.isPublishedCampaign()).isEqualTo(form.getPublishedCampaign());

  }

  @Test
  public void getForm_whenNoExistingEntity_thenEmptyFormReturned() {
    when(campaignInformationRepository.findByProjectDetail(projectDetail)).thenReturn(Optional.empty());
    CampaignInformationForm form = campaignInformationService.getForm(projectDetail);

    assertThat(form.getPublishedCampaign()).isNull();
    assertThat(form.getScopeDescription()).isNull();
  }

  @Test
  public void getForm_whenExistingEntity_thenPopulatedFormReturned() {
    var campaignInformation = CampaignInformationTestUtil.createCampaignInformation();
    when(campaignInformationRepository.findByProjectDetail(projectDetail)).thenReturn(Optional.of(campaignInformation));
    CampaignInformationForm form = campaignInformationService.getForm(projectDetail);

    assertThat(form.getPublishedCampaign()).isEqualTo(campaignInformation.isPublishedCampaign());
    assertThat(form.getScopeDescription()).isEqualTo(campaignInformation.getScopeDescription());
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getOrError_whenNotFound_thenException() {
    when(campaignInformationRepository.findByProjectDetail(projectDetail))
        .thenReturn(Optional.empty());

    campaignInformationService.getOrError(projectDetail);
  }

  @Test
  public void getOrError_whenFound_thenReturnCampaign() {
    var campaignInformation = CampaignInformationTestUtil.createCampaignInformation();
    when(campaignInformationRepository.findByProjectDetail(projectDetail))
        .thenReturn(Optional.of(campaignInformation));

    var result = campaignInformationService.getOrError(projectDetail);
    assertThat(result).isEqualTo(campaignInformation);
  }

  @Test
  public void getCampaignInformationModelAndView_assertCorrectModelProperties() {
    var form = new CampaignInformationForm();

    var modelAndView = campaignInformationService.getCampaignInformationModelAndView(
        projectDetail,
        form
    );
    assertThat(modelAndView.getViewName()).isEqualTo(CampaignInformationService.FORM_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("pageTitle", CampaignInformationController.PAGE_NAME),
        entry("form", form),
        entry(
            ProjectTypeModelUtil.PROJECT_TYPE_DISPLAY_NAME_MODEL_ATTR,
            projectDetail.getProjectType().getDisplayName()
        ),
        entry(
            ProjectTypeModelUtil.PROJECT_TYPE_LOWERCASE_DISPLAY_NAME_MODEL_ATTR,
            projectDetail.getProjectType().getLowercaseDisplayName()
        )
    );
  }

  @Test
  public void canShowInTaskList_true() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(projectDetail, ProjectTask.CAMPAIGN_INFORMATION)).thenReturn(true);
    assertThat(campaignInformationService.canShowInTaskList(projectDetail)).isTrue();
  }

  @Test
  public void canShowInTaskList_false() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(projectDetail, ProjectTask.CAMPAIGN_INFORMATION)).thenReturn(false);
    assertThat(campaignInformationService.canShowInTaskList(projectDetail)).isFalse();
  }

  @Test
  public void isComplete_whenInvalid_thenFalse() {

    final var bindingResult = new BeanPropertyBindingResult(CampaignInformationForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(validationService.validate(any(), any(), any(ValidationType.class))).thenReturn(bindingResult);

    final var isComplete = campaignInformationService.isComplete(projectDetail);

    assertThat(isComplete).isFalse();
  }

  @Test
  public void isComplete_whenValid_thenTrue() {
    when(validationService.validate(any(), any(), any(ValidationType.class)))
        .thenReturn(ReverseRouter.emptyBindingResult());

    final var isComplete = campaignInformationService.isComplete(projectDetail);

    assertThat(isComplete).isTrue();
  }

  @Test
  public void copySectionData_verifyInteractions() {

    final var campaignInformation = CampaignInformationTestUtil.createCampaignInformation();
    final var fromDetail = ProjectUtil.getProjectDetails();
    final var toDetail = ProjectUtil.getProjectDetails();

    when(campaignInformationRepository.findByProjectDetail(fromDetail))
        .thenReturn(Optional.of(campaignInformation));

    campaignInformationService.copySectionData(fromDetail, toDetail);

    verify(entityDuplicationService, times(1)).duplicateEntityAndSetNewParent(
        campaignInformationService.getOrError(fromDetail),
        toDetail,
        CampaignInformation.class
    );
  }

  @Test
  public void removeSectionData_verifyInteractions() {
    campaignInformationService.removeSectionData(projectDetail);

    verify(campaignInformationRepository, times(1)).deleteByProjectDetail(projectDetail);
  }

  @Test
  public void validate_fullValidation_verifyInteractions() {

    var form = new CampaignInformationForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    campaignInformationService.validate(
        form,
        bindingResult,
        ValidationType.FULL
    );

    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  public void validate_partialValidation_verifyInteractions() {

    var form = new CampaignInformationForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    campaignInformationService.validate(
        form,
        bindingResult,
        ValidationType.PARTIAL
    );

    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.PARTIAL);
  }
}