package uk.co.ogauthority.pathfinder.service.project.awardedcontract.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import javax.validation.Validation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractFormValidator;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.infrastructure.AwardedContractRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class AwardedContractServiceValidationTest {

  @Mock
  private AwardedContractRepository awardedContractRepository;

  @Mock
  private FunctionService functionService;

  @Mock
  private SearchSelectorService searchSelectorService;

  @Mock
  private ProjectSetupService projectSetupService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  @Mock
  private TeamService teamService;

  private AwardedContractService awardedContractService;

  @Before
  public void setup() {
    var validator = new SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator());
    var validationService = new ValidationService(validator);

    AwardedContractFormValidator awardedContractFormValidator = new AwardedContractFormValidator(
        new DateInputValidator());

    awardedContractService = new AwardedContractService(
        functionService,
        validationService,
        awardedContractRepository,
        awardedContractFormValidator,
        searchSelectorService,
        projectSetupService,
        entityDuplicationService,
        teamService);
  }

  @Test
  public void isComplete_whenValid_thenTrue() {

    var awardedContract1 = AwardedContractTestUtil.createAwardedContract();
    var awardedContract2 = AwardedContractTestUtil.createAwardedContract();
    var projectDetail = awardedContract1.getProjectDetail();

    when(awardedContractRepository.findByProjectDetailOrderByIdAsc(projectDetail)).thenReturn(
        List.of(awardedContract1, awardedContract2)
    );

    var isComplete = awardedContractService.isComplete(projectDetail);
    assertThat(isComplete).isTrue();
  }

  @Test
  public void isComplete_whenInvalid_thenFalse() {

    var awardedContract1 = AwardedContractTestUtil.createAwardedContract();
    var awardedContract2 = AwardedContractTestUtil.createAwardedContract();
    awardedContract2.setContractorName(null);

    var projectDetail = awardedContract1.getProjectDetail();

    when(awardedContractRepository.findByProjectDetailOrderByIdAsc(projectDetail)).thenReturn(
        List.of(awardedContract1, awardedContract2)
    );

    var isComplete = awardedContractService.isComplete(projectDetail);
    assertThat(isComplete).isFalse();
  }

  @Test
  public void isComplete_whenNoAwardedContracts_thenFalse() {

    var projectDetail = ProjectUtil.getProjectDetails();

    when(awardedContractRepository.findByProjectDetailOrderByIdAsc(projectDetail)).thenReturn(
        List.of()
    );

    var isComplete = awardedContractService.isComplete(projectDetail);
    assertThat(isComplete).isFalse();
  }

  @Test
  public void isValid_whenValid_thenTrue() {

    var awardedContract1 = AwardedContractTestUtil.createAwardedContract();
    var isValid = awardedContractService.isValid(awardedContract1, ValidationType.FULL);

    assertThat(isValid).isTrue();
  }

  @Test
  public void isValid_whenInvalid_thenFalse() {

    var awardedContract1 = AwardedContractTestUtil.createAwardedContract();
    awardedContract1.setContractorName(null);

    var isValid = awardedContractService.isValid(awardedContract1, ValidationType.FULL);

    assertThat(isValid).isFalse();
  }
}
