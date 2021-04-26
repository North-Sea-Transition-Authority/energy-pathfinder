package uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.Validation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.WorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.WorkPlanUpcomingTenderFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.workplanupcomingtender.WorkPlanUpcomingTenderRepository;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.WorkPlanUpcomingTenderUtil;

@RunWith(MockitoJUnitRunner.class)
public class WorkPlanUpcomingTenderServiceValidationTest {

  @Mock
  private BreadcrumbService breadcrumbService;

  @Mock
  private FunctionService functionService;

  @Mock
  private WorkPlanUpcomingTenderFormValidator workPlanUpcomingTenderFormValidator;

  @Mock
  private WorkPlanUpcomingTenderRepository workPlanUpcomingTenderRepository;

  @Mock
  private SearchSelectorService searchSelectorService;

  private WorkPlanUpcomingTenderService workPlanUpcomingTenderService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);

  private final WorkPlanUpcomingTender upcomingTender = WorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);


  @Before
  public void setup() {
    var validator = new SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator());
    var validationService = new ValidationService(validator);

    workPlanUpcomingTenderService = new WorkPlanUpcomingTenderService(
        breadcrumbService,
        functionService,
        validationService,
        workPlanUpcomingTenderFormValidator,
        workPlanUpcomingTenderRepository,
        searchSelectorService
    );
  }

  @Test
  public void isValid_whenValidForm_returnsTrue() {
    assertThat(workPlanUpcomingTenderService.isValid(upcomingTender, ValidationType.FULL)).isTrue();
  }

  @Test
  public void isValid_whenIncompleteForm_returnsFalse() {
    upcomingTender.setJobTitle(null);
    assertThat(workPlanUpcomingTenderService.isValid(upcomingTender, ValidationType.FULL)).isFalse();
  }
}
