package uk.co.ogauthority.pathfinder.service.project.integratedrig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.integratedrig.IntegratedRig;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.integratedrig.IntegratedRigForm;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.repository.project.integratedrig.IntegratedRigRepository;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFacilitiesService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.DevUkTestUtil;
import uk.co.ogauthority.pathfinder.testutil.IntegratedRigTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class IntegratedRigServiceTest {

  private IntegratedRigService integratedRigService;

  @Mock
  private DevUkFacilitiesService devUkFacilitiesService;

  @Mock
  private IntegratedRigRepository integratedRigRepository;

  @Mock
  private ValidationService validationService;

  private ProjectDetail projectDetail;

  @Before
  public void setup() {
    var searchSelectorService = new SearchSelectorService();
    integratedRigService = new IntegratedRigService(
        devUkFacilitiesService,
        integratedRigRepository,
        searchSelectorService,
        validationService
    );

    projectDetail = ProjectUtil.getProjectDetails();

    when(integratedRigRepository.save(any(IntegratedRig.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  private void checkCommonIntegratedRigEntityFields(IntegratedRig integratedRig,
                                                    IntegratedRigForm form) {
    assertThat(integratedRig.getName()).isEqualTo(form.getName());
    assertThat(integratedRig.getStatus()).isEqualTo(form.getStatus());
    assertThat(integratedRig.getIntentionToReactivate()).isEqualTo(form.getIntentionToReactivate());
  }

  private void assertCorrectionValidation(IntegratedRigForm form, ValidationType validationType) {

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    integratedRigService.validate(
        form,
        bindingResult,
        validationType
    );

    verify(validationService, times(1)).validate(form, bindingResult, validationType);
  }

  @Test
  public void getFacilityRestUrl() {
    integratedRigService.getFacilityRestUrl();
    verify(devUkFacilitiesService, times(1)).getFacilitiesRestUrl();
  }

  @Test
  public void getPreSelectedFacility() {
    var form = new IntegratedRigForm();
    form.setStructure("test");

    integratedRigService.getPreSelectedFacility(form);
    verify(devUkFacilitiesService, times(1)).getPreSelectedFacility(form.getStructure());
  }

  @Test
  public void validate_whenPartial() {
    var form = new IntegratedRigForm();
    assertCorrectionValidation(form, ValidationType.PARTIAL);
  }

  @Test
  public void validate_whenFull() {
    var form = new IntegratedRigForm();
    assertCorrectionValidation(form, ValidationType.FULL);
  }

  @Test
  public void createIntegratedRig_whenFacilityFromDevUk() {

    final var devUkFacilityId = 123;
    final var devUkFacility = DevUkTestUtil.getDevUkFacility(devUkFacilityId, "test");

    var form = IntegratedRigTestUtil.createIntegratedRigForm();
    form.setStructure(String.valueOf(devUkFacilityId));



    when(devUkFacilitiesService.getFacilityAsList(form.getStructure())).thenReturn(List.of(devUkFacility));

    var persistedIntegratedRig = integratedRigService.createIntegratedRig(projectDetail, form);

    assertThat(persistedIntegratedRig.getFacility()).isEqualTo(devUkFacility);
    assertThat(persistedIntegratedRig.getManualFacility()).isNull();
    checkCommonIntegratedRigEntityFields(persistedIntegratedRig, form);
  }

  @Test
  public void createIntegratedRig_whenFacilityIsManual() {

    final var manualEntryFacility = SearchSelectablePrefix.FREE_TEXT_PREFIX + "my manual facility";

    var form = IntegratedRigTestUtil.createIntegratedRigForm();
    form.setStructure(manualEntryFacility);

    var persistedIntegratedRig = integratedRigService.createIntegratedRig(projectDetail, form);

    assertThat(persistedIntegratedRig.getFacility()).isNull();
    assertThat(persistedIntegratedRig.getManualFacility()).isEqualTo(SearchSelectorService.removePrefix(manualEntryFacility));
    checkCommonIntegratedRigEntityFields(persistedIntegratedRig, form);
  }

}
