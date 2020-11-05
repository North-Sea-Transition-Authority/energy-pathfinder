package uk.co.ogauthority.pathfinder.service.project.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationForm;
import uk.co.ogauthority.pathfinder.service.portal.LicenceBlocksService;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class LicenceBlockValidatorServiceTest {

  public static final List<String> BLOCKS = List.of("12/34", "12/56");
  public static final String FIELD_ID = "field";
  @Mock
  private LicenceBlocksService licenceBlocksService;

  private LicenceBlockValidatorService licenceBlockValidatorService;

  private ProjectLocationForm form;

  @Before
  public void setUp() {
    licenceBlockValidatorService = new LicenceBlockValidatorService(
        licenceBlocksService
    );
    form = new ProjectLocationForm();
    form.setLicenceBlocks(BLOCKS);
  }

  @Test
  public void addErrorsForInvalidBlocks_allFound_noErrors() {
    when(licenceBlocksService.blockExists(any())).thenReturn(true);
    var errors = new BeanPropertyBindingResult(form, "form");
    licenceBlockValidatorService.addErrorsForInvalidBlocks(BLOCKS, errors, FIELD_ID);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void addErrorsForInvalidBlocks_oneNotFound_errorsExist() {
    when(licenceBlocksService.blockExists(BLOCKS.get(0))).thenReturn(false);
    var errors = new BeanPropertyBindingResult(form, "form");
    licenceBlockValidatorService.addErrorsForInvalidBlocks(BLOCKS, errors, FIELD_ID);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).hasSize(1);
    assertThat(fieldErrors).containsExactly(
        entry(FIELD_ID, Set.of(FIELD_ID + ".notPresent"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry(FIELD_ID, Set.of(
            LicenceBlockValidatorService.BLOCK_NOT_FOUND)
        )
    );
  }
}
