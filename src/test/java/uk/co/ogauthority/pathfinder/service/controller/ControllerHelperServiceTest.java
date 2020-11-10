package uk.co.ogauthority.pathfinder.service.controller;

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
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureDataJpa
@ActiveProfiles("integration-test")
public class ControllerHelperServiceTest {

  @Autowired
  private MessageSource messageSource;

  private ControllerHelperService controllerHelperService;

  private ModelAndView failedModelAndView;
  private ModelAndView passedModelAndView;

  @Before
  public void setup() {

    controllerHelperService = new ControllerHelperService(messageSource);

    failedModelAndView = new ModelAndView()
        .addObject("fail", true);

    passedModelAndView = new ModelAndView()
        .addObject("pass", true);

  }

  @Test
  public void checkErrorsAndRedirect_noErrors() {

    var form = new TypeMismatchTestForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var result = controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        failedModelAndView,
        form,
        () -> passedModelAndView
    );

    assertThat(result).isEqualTo(passedModelAndView);

  }

  @Test
  public void checkErrorsAndRedirect_errors() {

    var form = new TypeMismatchTestForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.rejectValue("integerField", "integerField.invalid", "Invalid value");
    bindingResult.rejectValue("stringField", "stringField.invalid", "Invalid string");

    var result = controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        failedModelAndView,
        form,
        () -> passedModelAndView
    );

    assertThat(result).isEqualTo(failedModelAndView);

    @SuppressWarnings("unchecked")
    var errorItemList = (List<ErrorItem>) result.getModel().get("errorList");

    assertThat(errorItemList)
        .extracting(ErrorItem::getDisplayOrder, ErrorItem::getFieldName, ErrorItem::getErrorMessage)
        .containsExactly(
            tuple(0, "integerField", "Invalid value"),
            tuple(1, "stringField", "Invalid string")
        );

  }

  @Test
  public void checkErrorsAndRedirect_errorListOrderSameAsForm() {
    var form = new FieldOrderTestForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.rejectValue("thirdField", "thirdField.invalid", "NotNull");
    bindingResult.rejectValue("secondField", "secondField.invalid", "NotNull");
    bindingResult.rejectValue("firstField", "firstField.invalid", "NotNull");

    var result = controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        failedModelAndView,
        form,
        () -> passedModelAndView
    );

    @SuppressWarnings("unchecked")
    var errorItemList = (List<ErrorItem>) result.getModel().get("errorList");

    assertThat(errorItemList)
        .extracting(ErrorItem::getDisplayOrder, ErrorItem::getFieldName, ErrorItem::getErrorMessage)
        .containsExactly(
            tuple(0, "firstField", "NotNull"),
            tuple(1, "secondField", "NotNull"),
            tuple(2, "thirdField", "NotNull")
        );
  }

  @Test
  public void checkErrorsAndRedirect_errorListOrderSameAsForm_ignoreSubField() {
    var form = new SubFieldTestForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.rejectValue("thirdField.month", "thirdField.invalid", "NotNull");
    bindingResult.rejectValue("secondField", "secondField.invalid", "NotNull");
    bindingResult.rejectValue("firstField.year", "firstField.invalid", "NotNull");

    var result = controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        failedModelAndView,
        form,
        () -> passedModelAndView
    );

    @SuppressWarnings("unchecked")
    var errorItemList = (List<ErrorItem>) result.getModel().get("errorList");

    assertThat(errorItemList)
        .extracting(ErrorItem::getDisplayOrder, ErrorItem::getFieldName, ErrorItem::getErrorMessage)
        .containsExactly(
            tuple(0, "firstField.year", "NotNull"),
            tuple(1, "secondField", "NotNull"),
            tuple(2, "thirdField.month", "NotNull")
        );
  }

  @Test
  public void checkErrorsAndRedirect_errorListOrderSameAsForm_nestedForm() {
    var form = new NestedTestForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.rejectValue("firstField", "firstField.invalid", "NotNull");
    bindingResult.rejectValue("thirdField", "thirdField.invalid", "NotNull");
    bindingResult.rejectValue("nestedForm.firstField", "firstField.invalid", "NotNull");
    bindingResult.rejectValue("nestedForm.secondField", "secondField.invalid", "NotNull");
    bindingResult.rejectValue("nestedForm.thirdField", "thirdField.invalid", "NotNull");

    var result = controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        failedModelAndView,
        form,
        () -> passedModelAndView
    );

    @SuppressWarnings("unchecked")
    var errorItemList = (List<ErrorItem>) result.getModel().get("errorList");

    assertThat(errorItemList)
        .extracting(ErrorItem::getDisplayOrder, ErrorItem::getFieldName, ErrorItem::getErrorMessage)
        .containsExactly(
            tuple(0, "firstField", "NotNull"),
            tuple(1, "nestedForm.firstField", "NotNull"),
            tuple(2, "nestedForm.secondField", "NotNull"),
            tuple(3, "nestedForm.thirdField", "NotNull"),
            tuple(4, "thirdField", "NotNull")
        );
  }

  @Test
  public void checkErrorsAndRedirect_errorListOrder_whenNoErrors_thenNullErrorList() {
    var form = new FieldOrderTestForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var result = controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        failedModelAndView,
        form,
        () -> passedModelAndView
    );

    @SuppressWarnings("unchecked")
    var errorItemList = (List<ErrorItem>) result.getModel().get("errorList");

    assertThat(errorItemList).isNull();
  }

  @Test
  public void checkErrorsAndRedirect_errorListOrder_whenNullFormButBindingErrors_thenNoSpecifiedOrdering() {
    var form = new FieldOrderTestForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.rejectValue("secondField", "secondField.invalid", "NotNull");
    bindingResult.rejectValue("firstField", "firstField.invalid", "NotNull");

    var result = controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        failedModelAndView,
        null,
        () -> passedModelAndView
    );

    @SuppressWarnings("unchecked")
    var errorItemList = (List<ErrorItem>) result.getModel().get("errorList");

    assertThat(errorItemList)
        .extracting(ErrorItem::getFieldName, ErrorItem::getErrorMessage)
        .containsExactlyInAnyOrder(
            tuple("secondField", "NotNull"),
            tuple("firstField", "NotNull")
        );
  }

  @Test
  public void checkErrorsAndRedirect_errorListOrder_whenFieldNotInForm_thenNoSpecifiedOrdering() {

    var formAttachedToBindingResult = new FieldOrderTestForm();
    var formPassedToErrorCheck = new TypeMismatchTestForm();

    var bindingResult = new BeanPropertyBindingResult(formAttachedToBindingResult, "form");
    bindingResult.rejectValue("secondField", "secondField.invalid", "NotNull");
    bindingResult.rejectValue("firstField", "firstField.invalid", "NotNull");

    var result = controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        failedModelAndView,
        formPassedToErrorCheck,
        () -> passedModelAndView
    );

    @SuppressWarnings("unchecked")
    var errorItemList = (List<ErrorItem>) result.getModel().get("errorList");

    assertThat(errorItemList)
        .extracting(ErrorItem::getFieldName, ErrorItem::getErrorMessage)
        .containsExactlyInAnyOrder(
            tuple("firstField", "NotNull"),
            tuple("secondField", "NotNull")
        );
  }

}
