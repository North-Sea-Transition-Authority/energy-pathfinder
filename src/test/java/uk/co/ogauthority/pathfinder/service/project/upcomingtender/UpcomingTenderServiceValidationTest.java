package uk.co.ogauthority.pathfinder.service.project.upcomingtender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import javax.validation.Validation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.upcomingtender.UpcomingTenderRepository;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderUtil;

@RunWith(MockitoJUnitRunner.class)
public class UpcomingTenderServiceValidationTest {

  @Mock
  private UpcomingTenderRepository upcomingTenderRepository;

  @Mock
  private UpcomingTenderFormValidator upcomingTenderFormValidator;

  @Mock
  private SearchSelectorService searchSelectorService;

  @Mock
  private FunctionService functionService;

  private UpcomingTenderService upcomingTenderService;

  private final ProjectDetail details = ProjectUtil.getProjectDetails();

  @Before
  public void setUp() {
    var validator = new SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator());
    var validationService = new ValidationService(validator);
    upcomingTenderService = new UpcomingTenderService(
        upcomingTenderRepository,
        validationService,
        upcomingTenderFormValidator,
        functionService,
        searchSelectorService
    );
  }

  @Test
  public void isCompleted_fullForm() {
    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(details);
    when(upcomingTenderRepository.findByProjectDetailOrderByIdAsc(any()))
        .thenReturn(Collections.singletonList(upcomingTender));
    assertThat(upcomingTenderService.isComplete(details)).isTrue();
  }

  @Test
  public void isCompleted_incompleteForm() {
    when(upcomingTenderRepository.findByProjectDetailOrderByIdAsc(any())).thenReturn(Collections.emptyList());
    assertThat(upcomingTenderService.isComplete(details)).isFalse();
  }

  @Test
  public void isValid_fullForm() {
    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(details);
    assertThat(upcomingTenderService.isValid(upcomingTender, ValidationType.FULL)).isTrue();
  }

  @Test
  public void isValid_incompleteForm() {
    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(details);
    upcomingTender.setJobTitle(null);
    assertThat(upcomingTenderService.isValid(upcomingTender, ValidationType.FULL)).isFalse();
  }
}
