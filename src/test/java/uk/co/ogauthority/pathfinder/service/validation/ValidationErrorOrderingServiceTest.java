package uk.co.ogauthority.pathfinder.service.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem;
import uk.co.ogauthority.pathfinder.service.controller.FieldOrderTestForm;
import uk.co.ogauthority.pathfinder.service.controller.ListTestForm;
import uk.co.ogauthority.pathfinder.service.controller.NestedTestForm;
import uk.co.ogauthority.pathfinder.service.controller.SubFieldTestForm;
import uk.co.ogauthority.pathfinder.service.controller.TestFormWithParentForm;
import uk.co.ogauthority.pathfinder.service.controller.TypeMismatchTestForm;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureDataJpa
@ActiveProfiles("integration-test")
public class ValidationErrorOrderingServiceTest {

  @Autowired
  private MessageSource messageSource;

  private ValidationErrorOrderingService validationErrorOrderingService;

  @Before
  public void setup() {
    validationErrorOrderingService = new ValidationErrorOrderingService(messageSource);
  }

  @Test
  public void getErrorItemsFromBindingResult_errorListOrderSameAsForm() {

    final var form = new FieldOrderTestForm();

    final var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.rejectValue("thirdField", "thirdField.invalid", "NotNull");
    bindingResult.rejectValue("secondField", "secondField.invalid", "NotNull");
    bindingResult.rejectValue("firstField", "firstField.invalid", "NotNull");

    final var resultingErrorItemList = validationErrorOrderingService.getErrorItemsFromBindingResult(
        form,
        bindingResult
    );

    assertThat(resultingErrorItemList)
        .extracting(ErrorItem::getDisplayOrder, ErrorItem::getFieldName, ErrorItem::getErrorMessage)
        .containsExactly(
            tuple(0, "firstField", "NotNull"),
            tuple(1, "secondField", "NotNull"),
            tuple(2, "thirdField", "NotNull")
        );
  }

  @Test
  public void getErrorItemsFromBindingResult_errorListOrderSameAsForm_ignoreSubField() {

    final var form = new SubFieldTestForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.rejectValue("thirdField.month", "thirdField.invalid", "NotNull");
    bindingResult.rejectValue("secondField", "secondField.invalid", "NotNull");
    bindingResult.rejectValue("firstField.year", "firstField.invalid", "NotNull");

    final var resultingErrorItemList = validationErrorOrderingService.getErrorItemsFromBindingResult(
        form,
        bindingResult
    );

    assertThat(resultingErrorItemList)
        .extracting(ErrorItem::getDisplayOrder, ErrorItem::getFieldName, ErrorItem::getErrorMessage)
        .containsExactly(
            tuple(0, "firstField.year", "NotNull"),
            tuple(1, "secondField", "NotNull"),
            tuple(2, "thirdField.month", "NotNull")
        );
  }

  @Test
  public void getErrorItemsFromBindingResult_errorListOrderSameAsForm_nestedForm() {

    final var form = new NestedTestForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.rejectValue("firstField", "firstField.invalid", "NotNull");
    bindingResult.rejectValue("thirdField", "thirdField.invalid", "NotNull");
    bindingResult.rejectValue("secondField.firstField", "firstField.invalid", "NotNull");
    bindingResult.rejectValue("secondField.secondField", "secondField.invalid", "NotNull");
    bindingResult.rejectValue("secondField.thirdField", "thirdField.invalid", "NotNull");

    final var resultingErrorItemList = validationErrorOrderingService.getErrorItemsFromBindingResult(
        form,
        bindingResult
    );

    assertThat(resultingErrorItemList)
        .extracting(ErrorItem::getDisplayOrder, ErrorItem::getFieldName, ErrorItem::getErrorMessage)
        .containsExactly(
            tuple(0, "firstField", "NotNull"),
            tuple(1, "secondField.firstField", "NotNull"),
            tuple(2, "secondField.secondField", "NotNull"),
            tuple(3, "secondField.thirdField", "NotNull"),
            tuple(4, "thirdField", "NotNull")
        );
  }

  @Test
  public void getErrorItemsFromBindingResult_errorListOrderSameAsForm_parentForm() {

    final var form = new TestFormWithParentForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.rejectValue("firstField", "firstField.invalid", "NotNull");
    bindingResult.rejectValue("thirdField", "thirdField.invalid", "NotNull");
    bindingResult.rejectValue("nonParentField", "nonParentField.invalid", "NotNull");

    final var resultingErrorItemList = validationErrorOrderingService.getErrorItemsFromBindingResult(
        form,
        bindingResult
    );

    assertThat(resultingErrorItemList)
        .extracting(ErrorItem::getDisplayOrder, ErrorItem::getFieldName, ErrorItem::getErrorMessage)
        .containsExactly(
            tuple(0, "nonParentField", "NotNull"),
            tuple(1, "firstField", "NotNull"),
            tuple(2, "thirdField", "NotNull")
        );
  }

  @Test
  public void getErrorItemsFromBindingResult_errorListOrderSameAsForm_list() {

    final var form = new ListTestForm();
    form.setListField(List.of(new NestedTestForm(), new NestedTestForm()));

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.rejectValue("listField", "listField.invalid", "NotNull");
    bindingResult.rejectValue("listField[0].firstField", "listField[0].invalid", "NotNull");

    final var resultingErrorItemList = validationErrorOrderingService.getErrorItemsFromBindingResult(
        form,
        bindingResult
    );

    assertThat(resultingErrorItemList)
        .extracting(ErrorItem::getDisplayOrder, ErrorItem::getFieldName, ErrorItem::getErrorMessage)
        .containsExactly(
            tuple(0, "listField", "NotNull"),
            tuple(1, "listField[0].firstField", "NotNull")
        );
  }

  @Test
  public void getErrorItemsFromBindingResult_errorListOrder_whenNoErrors_thenNullErrorList() {

    final var form = new FieldOrderTestForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    final var resultingErrorItemList = validationErrorOrderingService.getErrorItemsFromBindingResult(
        form,
        bindingResult
    );

    assertThat(resultingErrorItemList).isEmpty();
  }

  @Test
  public void getErrorItemsFromBindingResult_errorListOrder_whenNullFormButBindingErrors_thenNoSpecifiedOrdering() {

    final var form = new FieldOrderTestForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.rejectValue("secondField", "secondField.invalid", "NotNull");
    bindingResult.rejectValue("firstField", "firstField.invalid", "NotNull");

    final var resultingErrorItemList = validationErrorOrderingService.getErrorItemsFromBindingResult(
        form,
        bindingResult
    );

    assertThat(resultingErrorItemList)
        .extracting(ErrorItem::getFieldName, ErrorItem::getErrorMessage)
        .containsExactlyInAnyOrder(
            tuple("secondField", "NotNull"),
            tuple("firstField", "NotNull")
        );
  }

  @Test
  public void getErrorItemsFromBindingResult_errorListOrder_whenFieldNotInForm_thenNoSpecifiedOrdering() {

    final var formAttachedToBindingResult = new FieldOrderTestForm();
    final var formPassedToErrorCheck = new TypeMismatchTestForm();

    var bindingResult = new BeanPropertyBindingResult(formAttachedToBindingResult, "form");
    bindingResult.rejectValue("secondField", "secondField.invalid", "NotNull");
    bindingResult.rejectValue("firstField", "firstField.invalid", "NotNull");

    final var resultingErrorItemList = validationErrorOrderingService.getErrorItemsFromBindingResult(
        formPassedToErrorCheck,
        bindingResult
    );

    assertThat(resultingErrorItemList)
        .extracting(ErrorItem::getFieldName, ErrorItem::getErrorMessage)
        .containsExactlyInAnyOrder(
            tuple("firstField", "NotNull"),
            tuple("secondField", "NotNull")
        );
  }

  @Test
  public void getErrorItemsFromBindingResult_errorListOrder_whenOffsetProvided() {

    final var form = new FieldOrderTestForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.rejectValue("secondField", "secondField.invalid", "NotNull");
    bindingResult.rejectValue("firstField", "firstField.invalid", "NotNull");

    final var errorIndexOffset = 10;

    final var resultingErrorItemList = validationErrorOrderingService.getErrorItemsFromBindingResult(
        form,
        bindingResult,
        errorIndexOffset
    );

    assertThat(resultingErrorItemList)
        .extracting(ErrorItem::getDisplayOrder, ErrorItem::getFieldName, ErrorItem::getErrorMessage)
        .containsExactlyInAnyOrder(
            tuple(errorIndexOffset, "firstField", "NotNull"),
            tuple(errorIndexOffset + 1, "secondField", "NotNull")
        );
  }
}