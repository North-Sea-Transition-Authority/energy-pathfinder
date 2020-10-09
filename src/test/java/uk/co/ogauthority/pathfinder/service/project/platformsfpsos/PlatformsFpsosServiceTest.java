package uk.co.ogauthority.pathfinder.service.project.platformsfpsos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.platformsfpsos.PlatformFpso;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos.PlatformFpsoForm;
import uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos.PlatformFpsoFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.platformsfpsos.PlatformFpsoRepository;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFacilitiesService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.PlatformFpsoTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class PlatformsFpsosServiceTest {
  @Mock
  private PlatformFpsoRepository platformFpsoRepository;

  @Mock
  private DevUkFacilitiesService devUkFacilitiesService;

  @Mock
  private PlatformFpsoFormValidator platformFpsoFormValidator;

  @Mock
  private ValidationService validationService;

  private PlatformsFpsosService platformsFpsosService;

  private final ProjectDetail details = ProjectUtil.getProjectDetails();

  @Before
  public void setUp() throws Exception {
    platformsFpsosService = new PlatformsFpsosService(
        platformFpsoRepository,
        devUkFacilitiesService,
        new SearchSelectorService(),
        platformFpsoFormValidator,
        validationService
    );
    when(platformFpsoRepository.save(any(PlatformFpso.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void createPlatformFpso() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withSubstructuresToBeRemoved();
    when(devUkFacilitiesService.getOrError(any())).thenReturn(PlatformFpsoTestUtil.FACILITY);
    var platformFpso = platformsFpsosService.createPlatformFpso(details, form);
    assertThat(platformFpso.getProjectDetail()).isEqualTo(details);
    assertThat(platformFpso.getStructure()).isEqualTo(PlatformFpsoTestUtil.FACILITY);
    assertThat(platformFpso.getManualStructureName()).isNull();
    assertCommonFieldsMatch(platformFpso, form);
  }

  @Test
  public void createPlatformFpso_noSubStructureExpectedToBeRemoved() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_noSubstructuresToBeRemoved();
    when(devUkFacilitiesService.getOrError(any())).thenReturn(PlatformFpsoTestUtil.FACILITY);
    var platformFpso = platformsFpsosService.createPlatformFpso(details, form);
    assertThat(platformFpso.getProjectDetail()).isEqualTo(details);
    assertThat(platformFpso.getStructure()).isEqualTo(PlatformFpsoTestUtil.FACILITY);
    assertThat(platformFpso.getManualStructureName()).isNull();
    assertCommonFieldsMatch(platformFpso, form);
  }

  @Test
  public void createPlatformFpso_manualStructure() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withSubstructuresToBeRemoved_manualStructure();
    var platformFpso = platformsFpsosService.createPlatformFpso(details, form);
    assertThat(platformFpso.getProjectDetail()).isEqualTo(details);
    assertThat(platformFpso.getStructure()).isNull();
    assertThat(platformFpso.getManualStructureName()).isEqualTo(SearchSelectorService.removePrefix(form.getStructure()));
    assertCommonFieldsMatch(platformFpso, form);
  }

  @Test
  public void createPlatformFpso_noSubStructureExpectedToBeRemoved_manualStructure() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_noSubstructuresToBeRemoved_manualStructure();
    var platformFpso = platformsFpsosService.createPlatformFpso(details, form);
    assertThat(platformFpso.getProjectDetail()).isEqualTo(details);
    assertThat(platformFpso.getStructure()).isNull();
    assertThat(platformFpso.getManualStructureName()).isEqualTo(SearchSelectorService.removePrefix(form.getStructure()));
    assertCommonFieldsMatch(platformFpso, form);
  }

  @Test
  public void validate_whenPartial() {
    var form = new PlatformFpsoForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    platformsFpsosService.validate(
        form,
        bindingResult,
        ValidationType.PARTIAL
    );

    verify(validationService).validate(form, bindingResult, ValidationType.PARTIAL);
  }

  @Test
  public void validate_whenFull() {
    var form = new PlatformFpsoForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    platformsFpsosService.validate(
        form,
        bindingResult,
        ValidationType.FULL
    );

    verify(validationService).validate(form, bindingResult, ValidationType.FULL);
  }

  private void assertCommonFieldsMatch(PlatformFpso platformFpso, PlatformFpsoForm form) {
    assertThat(platformFpso.getTopsideFpsoMass()).isEqualTo(form.getTopsideFpsoMass());
    assertThat(platformFpso.getEarliestRemovalYear()).isEqualTo(form.getTopsideRemovalYears().getMinYear());
    assertThat(platformFpso.getLatestRemovalYear()).isEqualTo(form.getTopsideRemovalYears().getMaxYear());
    assertThat(platformFpso.getSubstructuresExpectedToBeRemoved()).isEqualTo(form.getSubstructureExpectedToBeRemoved());
    if (form.getSubstructureExpectedToBeRemoved()) {
      assertThat(platformFpso.getSubstructureRemovalPremise()).isEqualTo(form.getSubstructureRemovalPremise());
      assertThat(platformFpso.getSubstructureRemovalMass()).isEqualTo(form.getSubstructureRemovalMass());
      assertThat(platformFpso.getSubStructureRemovalEarliestYear()).isEqualTo(form.getSubstructureRemovalYears().getMinYear());
      assertThat(platformFpso.getSubStructureRemovalLatestYear()).isEqualTo(form.getSubstructureRemovalYears().getMaxYear());
    }
    if (!form.getSubstructureExpectedToBeRemoved()) {
      assertThat(platformFpso.getSubstructureRemovalPremise()).isNull();
      assertThat(platformFpso.getSubstructureRemovalMass()).isNull();
      assertThat(platformFpso.getSubStructureRemovalEarliestYear()).isNull();
      assertThat(platformFpso.getSubStructureRemovalLatestYear()).isNull();
    }
    assertThat(platformFpso.getFpsoType()).isEqualTo(form.getFpsoType());
    assertThat(platformFpso.getFpsoDimensions()).isEqualTo(form.getFpsoDimensions());
    assertThat(platformFpso.getFuturePlans()).isEqualTo(form.getFuturePlans());
  }

}
