package uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetupView;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractSectionSummaryService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@ExtendWith(MockitoExtension.class)
class ForwardWorkPlanAwardedContractSectionSummaryServiceTest {

  @Mock
  private ForwardWorkPlanAwardedContractService awardedContractService;

  @Mock
  private DifferenceService differenceService;

  @Mock
  private ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;

  @Mock
  private ProjectSectionItemOwnershipService projectSectionItemOwnershipService;

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  @Mock
  private ForwardWorkPlanAwardedContractSetupService setupService;

  private ForwardWorkPlanAwardedContractSectionSummaryService sectionSummaryService;

  private final ProjectType projectType = ProjectType.FORWARD_WORK_PLAN;
  private final ProjectDetail detail = ProjectUtil.getProjectDetails(projectType);

  @BeforeEach
  void setup() {
    sectionSummaryService = new ForwardWorkPlanAwardedContractSectionSummaryService(
        differenceService,
        projectSectionSummaryCommonModelService,
        projectSectionItemOwnershipService,
        portalOrganisationAccessor,
        awardedContractService,
        setupService
    );
  }

  @Test
  void canShowSection_whenCanShowInTaskList_thenTrue() {
    assertThat(sectionSummaryService.canShowSection(detail)).isTrue();
  }

  @Test
  void canShowSection_whenCannotShowInTaskList_thenFalse() {
    var infrastructureProjectDetails = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);
    assertThat(sectionSummaryService.canShowSection(infrastructureProjectDetails)).isFalse();
  }

  @Test
  void getSummary() {
    when(awardedContractService.getAwardedContracts(detail)).thenReturn(List.of(
        AwardedContractTestUtil.createForwardWorkPlanAwardedContract(),
        AwardedContractTestUtil.createForwardWorkPlanAwardedContract()
    ));

    when(awardedContractService.getAwardedContractsByProjectAndVersion(
        detail.getProject(),
        detail.getVersion() - 1
    )).thenReturn(List.of(
        AwardedContractTestUtil.createForwardWorkPlanAwardedContract(),
        AwardedContractTestUtil.createForwardWorkPlanAwardedContract()
    ));

    when(setupService.getAwardedContractSetupView(detail))
        .thenReturn(AwardedContractTestUtil.createForwardWorkPlanAwardedContractSetupView());

    when(setupService.getAwardedContractSetupView(
        detail.getProject(),
        detail.getVersion() -1 )
    ).thenReturn(new ForwardWorkPlanAwardedContractSetupView());

    var sectionSummary = sectionSummaryService.getSummary(detail);

    AwardedContractTestUtil.assertForwardWorkPlanModelProperties(
        sectionSummary,
        ForwardWorkPlanAwardedContractSectionSummaryService.TEMPLATE_PATH,
        ForwardWorkPlanAwardedContractSectionSummaryService.DISPLAY_ORDER
    );

    verify(projectSectionSummaryCommonModelService, times(1)).getCommonSummaryModelMap(
        detail,
        AwardedContractSectionSummaryService.PAGE_NAME,
        AwardedContractSectionSummaryService.SECTION_ID
    );

    verify(differenceService, times(1)).differentiateComplexLists(
        any(),
        any(),
        eq(Set.of("summaryLinks")),
        any(),
        any()
    );
  }

  @Test
  void getSummary_noAwardedContracts() {
    when(awardedContractService.getAwardedContracts(detail)).thenReturn(Collections.emptyList());

    var sectionSummary = sectionSummaryService.getSummary(detail);

    AwardedContractTestUtil.assertForwardWorkPlanModelProperties(
        sectionSummary,
        ForwardWorkPlanAwardedContractSectionSummaryService.TEMPLATE_PATH,
        ForwardWorkPlanAwardedContractSectionSummaryService.DISPLAY_ORDER
    );

    verify(projectSectionSummaryCommonModelService, times(1)).getCommonSummaryModelMap(
        detail,
        AwardedContractSectionSummaryService.PAGE_NAME,
        AwardedContractSectionSummaryService.SECTION_ID
    );
  }

  @Test
  void getAwardedContractView() {
    var projectDetail = ProjectUtil.getProjectDetails(projectType);
    var awardedContract = AwardedContractTestUtil.createForwardWorkPlanAwardedContract();
    awardedContract.setProjectDetail(projectDetail);

    var result = sectionSummaryService.getAwardedContractView(awardedContract, 1);
    var expectedView = AwardedContractTestUtil.createForwardWorkPlanAwardedContractView(1);
    assertThat(result).isEqualTo(expectedView);
  }
}
