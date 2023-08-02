package uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderController;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderConversionController;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.duration.DurationPeriod;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanUpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@ExtendWith(MockitoExtension.class)
class ForwardWorkPlanUpcomingTenderViewUtilTest {

  private final PortalOrganisationGroup addedByPortalOrganisationGroup =
      TeamTestingUtil.generateOrganisationGroup(1, "org", "org");

  private ProjectDetail projectDetail;

  @BeforeEach
  void setup() {
    projectDetail = ProjectUtil.getProjectDetails();
  }

  @Test
  void createUpcomingTenderView_whenNoTenderDepartment_thenEmptyStringWithTag() {
    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setDepartmentType(null);
    upcomingTender.setManualDepartmentType(null);

    final var displayOrder = 1;
    var includeSummaryLinks = true;
    ForwardWorkPlanUpcomingTenderView upcomingTenderView = new ForwardWorkPlanUpcomingTenderViewUtil.ForwardWorkPlanUpcomingTenderViewBuilder(
        upcomingTender,
        displayOrder,
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(includeSummaryLinks)
        .build();

    assertThat(upcomingTenderView.getTenderDepartment()).isEqualTo(new StringWithTag());

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  void createUpComingTenderView_whenFromListTenderDepartment_thenFromListStringWithTag() {
    final var tenderDepartment = Function.DRILLING;

    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setDepartmentType(tenderDepartment);
    upcomingTender.setManualDepartmentType(null);

    final var displayOrder = 2;

    var includeSummaryLinks = true;
    ForwardWorkPlanUpcomingTenderView upcomingTenderView = new ForwardWorkPlanUpcomingTenderViewUtil.ForwardWorkPlanUpcomingTenderViewBuilder(
        upcomingTender,
        displayOrder,
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(includeSummaryLinks)
        .build();

    assertThat(upcomingTenderView.getTenderDepartment()).isEqualTo(
        new StringWithTag(tenderDepartment.getDisplayName(), Tag.NONE)
    );

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  void createUpComingTenderView_whenNotFromListTenderDepartment_thenNotFromListStringWithTag() {
    final var tenderDepartment = "Manual entry";

    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setDepartmentType(null);
    upcomingTender.setManualDepartmentType(tenderDepartment);

    final var displayOrder = 2;

    var includeSummaryLinks = true;
    ForwardWorkPlanUpcomingTenderView upcomingTenderView = new ForwardWorkPlanUpcomingTenderViewUtil.ForwardWorkPlanUpcomingTenderViewBuilder(
        upcomingTender,
        displayOrder,
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(includeSummaryLinks)
        .build();

    assertThat(upcomingTenderView.getTenderDepartment()).isEqualTo(
        new StringWithTag(tenderDepartment, Tag.NOT_FROM_LIST)
    );

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  void createUpComingTenderView_whenContractBandProvided_thenContractBandPopulated() {
    final var contractBand = ContractBand.GREATER_THAN_OR_EQUAL_TO_5M;

    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setContractBand(contractBand);

    final var displayOrder = 2;

    var includeSummaryLinks = true;
    ForwardWorkPlanUpcomingTenderView upcomingTenderView = new ForwardWorkPlanUpcomingTenderViewUtil.ForwardWorkPlanUpcomingTenderViewBuilder(
        upcomingTender,
        displayOrder,
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(includeSummaryLinks)
        .build();

    assertThat(upcomingTenderView.getContractBand()).isEqualTo(contractBand.getDisplayName());

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  void createUpComingTenderView_whenContractBandNotProvided_thenEmptyString() {
    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setContractBand(null);

    final var displayOrder = 2;

    var includeSummaryLinks = true;
    ForwardWorkPlanUpcomingTenderView upcomingTenderView = new ForwardWorkPlanUpcomingTenderViewUtil.ForwardWorkPlanUpcomingTenderViewBuilder(
        upcomingTender,
        displayOrder,
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(includeSummaryLinks)
        .build();

    assertThat(upcomingTenderView.getContractBand()).isEmpty();

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  void createUpComingTenderView_whenIsValidTrue_thenIsValidTrueInView() {
    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);

    final var displayOrder = 3;
    final var isValid = true;

    var includeSummaryLinks = true;
    ForwardWorkPlanUpcomingTenderView upcomingTenderView = new ForwardWorkPlanUpcomingTenderViewUtil.ForwardWorkPlanUpcomingTenderViewBuilder(
        upcomingTender,
        displayOrder,
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(includeSummaryLinks)
        .isValid(isValid)
        .build();

    assertThat(upcomingTenderView.isValid()).isTrue();

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  void createUpComingTenderView_whenIsValidFalse_thenIsValidFalseInView() {
    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);

    final var displayOrder = 3;
    final var isValid = false;
    var includeSummaryLinks = true;
    ForwardWorkPlanUpcomingTenderView upcomingTenderView = new ForwardWorkPlanUpcomingTenderViewUtil.ForwardWorkPlanUpcomingTenderViewBuilder(
        upcomingTender,
        displayOrder,
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(includeSummaryLinks)
        .isValid(isValid)
        .build();

    assertThat(upcomingTenderView.isValid()).isFalse();

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  void createUpcomingTenderView_whenContractTermDurationAndPeriodNull_thenContractLengthSetToEmptyString() {
    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setContractTermDurationPeriod(null);
    upcomingTender.setContractTermDuration(null);

    final var displayOrder = 1;

    var includeSummaryLinks = true;
    ForwardWorkPlanUpcomingTenderView upcomingTenderView = new ForwardWorkPlanUpcomingTenderViewUtil.ForwardWorkPlanUpcomingTenderViewBuilder(
        upcomingTender,
        displayOrder,
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(includeSummaryLinks)
        .build();

    assertThat(upcomingTenderView.getContractLength()).isEmpty();
    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  void createUpcomingTenderView_whenContractTermDurationPeriodNull_thenContractLengthSetToEmptyString() {
    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setContractTermDurationPeriod(null);
    upcomingTender.setContractTermDuration(10);

    final var displayOrder = 1;

    var includeSummaryLinks = true;
    ForwardWorkPlanUpcomingTenderView upcomingTenderView = new ForwardWorkPlanUpcomingTenderViewUtil.ForwardWorkPlanUpcomingTenderViewBuilder(
        upcomingTender,
        displayOrder,
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(includeSummaryLinks)
        .build();

    assertThat(upcomingTenderView.getContractLength()).isEmpty();
    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  void createUpcomingTenderView_whenContractTermDurationNull_thenContractLengthSetToEmptyString() {
    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setContractTermDurationPeriod(DurationPeriod.DAYS);
    upcomingTender.setContractTermDuration(null);

    final var displayOrder = 1;

    var includeSummaryLinks = true;
    ForwardWorkPlanUpcomingTenderView upcomingTenderView = new ForwardWorkPlanUpcomingTenderViewUtil.ForwardWorkPlanUpcomingTenderViewBuilder(
        upcomingTender,
        displayOrder,
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(includeSummaryLinks)
        .build();

    assertThat(upcomingTenderView.getContractLength()).isEmpty();
    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  @Test
  void createUpcomingTenderView_whenNotIncludeSummaryLinks_thenNoLinks() {
    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);

    final var displayOrder = 1;
    var includeSummaryLinks = false;

    ForwardWorkPlanUpcomingTenderView upcomingTenderView = new ForwardWorkPlanUpcomingTenderViewUtil.ForwardWorkPlanUpcomingTenderViewBuilder(
        upcomingTender,
        displayOrder,
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(includeSummaryLinks)
        .build();

    assertThat(upcomingTenderView.getSummaryLinks()).isEmpty();
  }

  @Test
  void createUpcomingTenderView_whenContractTermDurationAbove1AndPeriodProvided_assertPluralContractLength() {
    final var contractTermDuration = 10;
    final var contractDurationPeriod = DurationPeriod.WEEKS;
    final var expectedContractLength = String.format(
        "%s %s",
        contractTermDuration,
        contractDurationPeriod.getDisplayNamePlural().toLowerCase()
    );

    assertExpectedContractTerm(contractTermDuration, contractDurationPeriod, expectedContractLength);
  }

  @Test
  void createUpcomingTenderView_whenContractTermDurationEqual1AndPeriodProvided_assertSingularContractLength() {
    final var contractTermDuration = 1;
    final var contractDurationPeriod = DurationPeriod.DAYS;
    final var expectedContractLength = String.format(
        "%s %s",
        contractTermDuration,
        contractDurationPeriod.getDisplayNameSingular().toLowerCase()
    );

    assertExpectedContractTerm(contractTermDuration, contractDurationPeriod, expectedContractLength);
  }

  @Test
  void createUpComingTenderView_whenAddedByPortalOrgIsEmpty_thenDefaultAddedByString() {
    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    var displayOrder = 2;
    var includeSummaryLinks = true;
    var emptyPortalOrganisationGroup = new PortalOrganisationGroup();
    ForwardWorkPlanUpcomingTenderView upcomingTenderView = new ForwardWorkPlanUpcomingTenderViewUtil.ForwardWorkPlanUpcomingTenderViewBuilder(
        upcomingTender,
        displayOrder,
        emptyPortalOrganisationGroup
    )
        .includeSummaryLinks(includeSummaryLinks)
        .build();

    assertThat(upcomingTenderView.getAddedByPortalOrganisationGroup()).isEqualTo("Unknown organisation");
  }

  private void assertExpectedContractTerm(Integer contractTermDuration,
                                          DurationPeriod contractTermDurationPeriod,
                                          String expectedContractTermString) {
    var upcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setContractTermDurationPeriod(contractTermDurationPeriod);
    upcomingTender.setContractTermDuration(contractTermDuration);

    final var displayOrder = 1;

    var includeSummaryLinks = true;
    ForwardWorkPlanUpcomingTenderView upcomingTenderView = new ForwardWorkPlanUpcomingTenderViewUtil.ForwardWorkPlanUpcomingTenderViewBuilder(
        upcomingTender,
        displayOrder,
        addedByPortalOrganisationGroup
    )
        .includeSummaryLinks(includeSummaryLinks)
        .build();

    assertThat(upcomingTenderView.getContractLength()).isEqualTo(expectedContractTermString);
    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder);
  }

  private void assertCommonProperties(
      ForwardWorkPlanUpcomingTenderView upcomingTenderView,
      ForwardWorkPlanUpcomingTender upcomingTender,
      int displayOrder
  ) {
    assertThat(upcomingTenderView.getDescriptionOfWork()).isEqualTo(upcomingTender.getDescriptionOfWork());
    assertThat(upcomingTenderView.getEstimatedTenderStartDate()).isEqualTo(DateUtil.getDateFromQuarterYear(upcomingTender.getEstimatedTenderDateQuarter(), upcomingTender.getEstimatedTenderDateYear()));
    assertThat(upcomingTenderView.getContactName()).isEqualTo(upcomingTender.getName());
    assertThat(upcomingTenderView.getContactPhoneNumber()).isEqualTo(upcomingTender.getPhoneNumber());
    assertThat(upcomingTenderView.getContactEmailAddress()).isEqualTo(upcomingTender.getEmailAddress());
    assertThat(upcomingTenderView.getContactJobTitle()).isEqualTo(upcomingTender.getJobTitle());
    assertThat(upcomingTenderView.getProjectId()).isEqualTo(upcomingTender.getProjectDetail().getProject().getId());
    assertThat(upcomingTenderView.getDisplayOrder()).isEqualTo(displayOrder);
    assertThat(upcomingTenderView.getAddedByPortalOrganisationGroup()).isEqualTo(addedByPortalOrganisationGroup.getName());

    final var editSummaryLink = new SummaryLink(
        SummaryLinkText.EDIT.getDisplayName(),
        ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class).editUpcomingTender(
            upcomingTenderView.getProjectId(),
            upcomingTenderView.getId(),
            null
        ))
    );

    final var convertLink = new SummaryLink(
        SummaryLinkText.CONVERT_TO_AWARDED.getDisplayName(),
        ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderConversionController.class).convertUpcomingTenderConfirm(
            upcomingTenderView.getProjectId(),
            upcomingTenderView.getId(),
            displayOrder,
            null
        ))
    );

    final var removeSummaryLink = new SummaryLink(
        SummaryLinkText.DELETE.getDisplayName(),
        ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class).removeUpcomingTenderConfirm(
            upcomingTenderView.getProjectId(),
            upcomingTenderView.getId(),
            displayOrder,
            null
        ))
    );

    assertThat(upcomingTenderView.getSummaryLinks()).containsExactly(editSummaryLink, convertLink, removeSummaryLink);
  }
}
