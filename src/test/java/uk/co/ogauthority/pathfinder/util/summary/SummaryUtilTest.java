package uk.co.ogauthority.pathfinder.util.summary;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@RunWith(MockitoJUnitRunner.class)
public class SummaryUtilTest {

  private static final String FIELD_NAME_ERROR = "field name error";
  private static final String EMPTY_LIST_ERROR = "Empty list error";
  private static final String ERROR_MSG = "error message";

  private static final List<SummaryItemTestImpl> VALID_VIEWS = List.of(
      new SummaryItemTestImpl(1, true),
      new SummaryItemTestImpl(2, true)
    );

  private static final List<SummaryItemTestImpl> INVALID_VIEWS = List.of(
      new SummaryItemTestImpl(1, false),
      new SummaryItemTestImpl(2, true)
    );



  @Test
  public void getErrors_emptyList_noErrors() {
    var errors = SummaryUtil.getErrors(
          Collections.emptyList(),
          EMPTY_LIST_ERROR,
          FIELD_NAME_ERROR,
          ERROR_MSG
        );

    assertThat(errors.size()).isEqualTo(1);
    assertThat(errors.get(0).getDisplayOrder()).isEqualTo(SummaryUtil.DEFAULT_DISPLAY_ORDER);
    assertThat(errors.get(0).getFieldName()).isEqualTo(EMPTY_LIST_ERROR);
    assertThat(errors.get(0).getErrorMessage()).isEqualTo(EMPTY_LIST_ERROR);
  }

  @Test
  public void getErrors_singleError_containsError() {
    var errors = SummaryUtil.getErrors(
        new ArrayList<>(INVALID_VIEWS),
        EMPTY_LIST_ERROR,
        FIELD_NAME_ERROR,
        ERROR_MSG
    );

    assertThat(errors.size()).isEqualTo(1);
    assertThat(errors.get(0).getDisplayOrder()).isEqualTo(SummaryUtil.DEFAULT_DISPLAY_ORDER);
    assertThat(errors.get(0).getFieldName()).isEqualTo(FIELD_NAME_ERROR);
    assertThat(errors.get(0).getErrorMessage()).isEqualTo(ERROR_MSG);
  }

  @Test
  public void getErrors_allValid_noErrors() {
    var errors = SummaryUtil.getErrors(
        new ArrayList<>(VALID_VIEWS),
        EMPTY_LIST_ERROR,
        FIELD_NAME_ERROR,
        ERROR_MSG
    );

    assertThat(errors.isEmpty()).isTrue();
  }

  @Test
  public void validateViews_emptyList_isInvalid() {
    assertThat(SummaryUtil.validateViews(Collections.emptyList())).isEqualTo(ValidationResult.INVALID);
  }

  @Test
  public void validateViews_invalidView_isInvalid() {
    assertThat(SummaryUtil.validateViews(new ArrayList<>(INVALID_VIEWS))).isEqualTo(ValidationResult.INVALID);
  }

  @Test
  public void validateViews_allValid_isValid() {
    assertThat(SummaryUtil.validateViews(new ArrayList<>(VALID_VIEWS))).isEqualTo(ValidationResult.VALID);
  }
}
