package uk.co.ogauthority.pathfinder.service.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import javax.validation.Validation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.selectoperator.SelectOperatorService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class SelectOperatorServiceValidationTest {

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  @Mock
  private SearchSelectorService searchSelectorService;

  @Mock
  private ProjectOperatorService projectOperatorService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  private SelectOperatorService selectOperatorService;

  private static final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private static final PortalOrganisationGroup organisationGroup = TeamTestingUtil.generateOrganisationGroup(
      1,
      "Org Grp",
      "Org Grp"
  );

  private static final ProjectOperator projectOperator = ProjectOperatorTestUtil.getOperator(detail, organisationGroup);


  @Before
  public void setUp() {
    var validator = new SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator());
    var validationService = new ValidationService(validator);
    selectOperatorService = new SelectOperatorService(
        portalOrganisationAccessor,
        validationService,
        searchSelectorService,
        projectOperatorService,
        entityDuplicationService
    );
  }

  @Test
  public void isCompleted_fullForm() {
    when(projectOperatorService.getProjectOperatorByProjectDetail(detail))
        .thenReturn(Optional.of(projectOperator));
    assertThat(selectOperatorService.isComplete(detail)).isTrue();
  }

  //There should not be a scenario where there isn't a complete ProjectOperator
  @Test(expected = PathfinderEntityNotFoundException.class)
  public void isCompleted_incompleteForm() {
    when(projectOperatorService.getProjectOperatorByProjectDetail(detail))
        .thenReturn(Optional.empty());
    selectOperatorService.isComplete(detail);
  }
}
