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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.ModelAndView;
import uk.co.fivium.energyportal.starter.accounts.EnergyPortalServiceAccessService;
import uk.co.fivium.energyportal.starter.configuration.WellKnownOrganisationGroupsConfigurationProperties;
import uk.co.fivium.energyportalapi.client.EnergyPortal;
import uk.co.fivium.energyportalapi.client.organisation.OrganisationApi;
import uk.co.fivium.energyportalmessagequeue.sns.SnsService;
import uk.co.fivium.energyportalmessagequeue.sqs.SqsService;
import uk.co.ogauthority.pathfinder.energyportal.epa.EnergyPortalApiConfig;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.organisationgroup.OrganisationGroupQueryService;
import uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem;
import uk.co.ogauthority.pathfinder.service.validation.ValidationErrorOrderingService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureDataJpa
@TestPropertySource(properties = "spring.autoconfigure.exclude=uk.co.fivium.digitalenummaterialisationlibrary.configuration.AutoConfiguration")
@ActiveProfiles("integration-test")
public class ControllerHelperServiceTest {

  @Autowired
  private ValidationErrorOrderingService validationErrorOrderingService;

  @MockitoBean
  private EnergyPortalServiceAccessService energyPortalServiceAccessService;

  @MockitoBean
  private SqsService sqsService;

  @MockitoBean
  private SnsService snsService;

  @MockitoBean
  private EnergyPortal energyPortal;

  @MockitoBean
  private EnergyPortalApiConfig energyPortalApiConfig;

  @MockitoBean
  private OrganisationApi organisationApi;

  @MockitoBean
  private OrganisationGroupQueryService organisationGroupQueryService;

  @MockitoBean
  private WellKnownOrganisationGroupsConfigurationProperties wellKnownOrganisationGroupsConfigurationProperties;

  private ControllerHelperService controllerHelperService;

  private ModelAndView failedModelAndView;
  private ModelAndView passedModelAndView;

  @Before
  public void setup() {

    controllerHelperService = new ControllerHelperService(validationErrorOrderingService);

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
}