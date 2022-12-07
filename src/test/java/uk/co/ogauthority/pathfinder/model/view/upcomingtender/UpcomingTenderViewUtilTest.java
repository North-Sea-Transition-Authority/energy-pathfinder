package uk.co.ogauthority.pathfinder.model.view.upcomingtender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.project.upcomingtender.UpcomingTendersController;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.UploadedFileUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class UpcomingTenderViewUtilTest {

  private PortalOrganisationGroup addedByPortalOrganisationGroup = TeamTestingUtil.generateOrganisationGroup(1, "org", "org");
  private ProjectDetail projectDetail;
  private List<UploadedFileView> uploadedFileViews;

  @Before
  public void setup() {
    projectDetail = ProjectUtil.getProjectDetails();
    uploadedFileViews = List.of(UploadedFileUtil.createUploadedFileView());

  }

  @Test
  public void createUpComingTenderView_whenNoTenderFunction_thenEmptyStringWithTag() {

    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setTenderFunction(null);
    upcomingTender.setManualTenderFunction(null);

    final var displayOrder = 1;

    UpcomingTenderView upcomingTenderView = new UpcomingTenderViewUtil.UpcomingTenderViewBuilder(
        upcomingTender,
        displayOrder,
        uploadedFileViews,
        addedByPortalOrganisationGroup
    )
        .isValid(true)
        .includeSummaryLinks(true)
        .build();

    assertThat(upcomingTenderView.getTenderFunction()).isEqualTo(new StringWithTag());

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder, uploadedFileViews, true);

  }

  @Test
  public void createUpComingTenderView_whenFromListTenderFunction_thenFromListStringWithTag() {

    final var tenderFunction = Function.DRILLING;

    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setTenderFunction(tenderFunction);
    upcomingTender.setManualTenderFunction(null);

    final var displayOrder = 2;

    UpcomingTenderView upcomingTenderView = new UpcomingTenderViewUtil.UpcomingTenderViewBuilder(
        upcomingTender,
        displayOrder,
        uploadedFileViews,
        addedByPortalOrganisationGroup
    )
        .isValid(true)
        .includeSummaryLinks(true)
        .build();

    assertThat(upcomingTenderView.getTenderFunction()).isEqualTo(
        new StringWithTag(tenderFunction.getDisplayName(), Tag.NONE)
    );

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder, uploadedFileViews, true);

  }

  @Test
  public void createUpComingTenderView_whenNotFromListTenderFunction_thenNotFromListStringWithTag() {

    final var tenderFunction = "Manual entry";

    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setTenderFunction(null);
    upcomingTender.setManualTenderFunction(tenderFunction);

    final var displayOrder = 2;

    UpcomingTenderView upcomingTenderView = new UpcomingTenderViewUtil.UpcomingTenderViewBuilder(
        upcomingTender,
        displayOrder,
        uploadedFileViews,
        addedByPortalOrganisationGroup
    )
        .isValid(true)
        .includeSummaryLinks(true)
        .build();

    assertThat(upcomingTenderView.getTenderFunction()).isEqualTo(
        new StringWithTag(tenderFunction, Tag.NOT_FROM_LIST)
    );

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder, uploadedFileViews, true);

  }

  @Test
  public void createUpComingTenderView_whenContractBandProvided_thenContractBandPopulated() {

    final var contractBand = ContractBand.LESS_THAN_25M;

    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setContractBand(contractBand);

    final var displayOrder = 2;

    UpcomingTenderView upcomingTenderView = new UpcomingTenderViewUtil.UpcomingTenderViewBuilder(
        upcomingTender,
        displayOrder,
        uploadedFileViews,
        addedByPortalOrganisationGroup
    )
        .isValid(true)
        .includeSummaryLinks(true)
        .build();

    assertThat(upcomingTenderView.getContractBand()).isEqualTo(contractBand.getDisplayName());

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder, uploadedFileViews, true);

  }

  @Test
  public void createUpComingTenderView_whenContractBandNotProvided_thenEmptyString() {

    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender.setContractBand(null);

    final var displayOrder = 2;

    UpcomingTenderView upcomingTenderView = new UpcomingTenderViewUtil.UpcomingTenderViewBuilder(
        upcomingTender,
        displayOrder,
        uploadedFileViews,
        addedByPortalOrganisationGroup
    )
        .isValid(true)
        .includeSummaryLinks(true)
        .build();

    assertThat(upcomingTenderView.getContractBand()).isEmpty();

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder, uploadedFileViews, true);

  }

  @Test
  public void createUpComingTenderView_whenIsValidTrue_thenIsValidTrueInView() {

    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(projectDetail);

    final var displayOrder = 3;
    final var isValid = true;

    UpcomingTenderView upcomingTenderView = new UpcomingTenderViewUtil.UpcomingTenderViewBuilder(
        upcomingTender,
        displayOrder,
        uploadedFileViews,
        addedByPortalOrganisationGroup
    )
        .isValid(isValid)
        .includeSummaryLinks(true)
        .build();

    assertThat(upcomingTenderView.isValid()).isTrue();

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder, uploadedFileViews, true);

  }

  @Test
  public void createUpComingTenderView_whenIsValidFalse_thenIsValidFalseInView() {

    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(projectDetail);

    final var displayOrder = 3;
    final var isValid = false;

    UpcomingTenderView upcomingTenderView = new UpcomingTenderViewUtil.UpcomingTenderViewBuilder(
        upcomingTender,
        displayOrder,
        uploadedFileViews,
        addedByPortalOrganisationGroup
    )
        .isValid(isValid)
        .includeSummaryLinks(true)
        .build();

    assertThat(upcomingTenderView.isValid()).isFalse();

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder, uploadedFileViews, true);

  }

  @Test
  public void createUpComingTenderView_doNotIncludeSummaryLinks_thenEmptyLinkList() {

    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(projectDetail);
    var includeSummaryLinks = false;

    final var displayOrder = 2;

    UpcomingTenderView upcomingTenderView = new UpcomingTenderViewUtil.UpcomingTenderViewBuilder(
        upcomingTender,
        displayOrder,
        uploadedFileViews,
        addedByPortalOrganisationGroup
    )
        .isValid(true)
        .includeSummaryLinks(includeSummaryLinks)
        .build();

    assertCommonProperties(upcomingTenderView, upcomingTender, displayOrder, uploadedFileViews, includeSummaryLinks);

  }

  @Test
  public void createUpComingTenderView_cannotFindPortalOrgGroup_thenDefaultAddedByPortalOrgGroupString() {
    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(projectDetail);
    var includeSummaryLinks = false;
    var emptyPortalOrganisationGroup = new PortalOrganisationGroup();
    var displayOrder = 2;

    UpcomingTenderView upcomingTenderView = new UpcomingTenderViewUtil.UpcomingTenderViewBuilder(
        upcomingTender,
        displayOrder,
        uploadedFileViews,
        emptyPortalOrganisationGroup
    )
        .isValid(true)
        .includeSummaryLinks(includeSummaryLinks)
        .build();

    assertThat(upcomingTenderView.getAddedByOrganisationGroup()).isEqualTo("Unknown organisation");
  }

  private void assertCommonProperties(
      UpcomingTenderView upcomingTenderView,
      UpcomingTender upcomingTender,
      int displayOrder,
      List<UploadedFileView> files,
      boolean canAccessTenderLinks
  ) {
    assertThat(upcomingTenderView.getDescriptionOfWork()).isEqualTo(upcomingTender.getDescriptionOfWork());
    assertThat(upcomingTenderView.getEstimatedTenderDate()).isEqualTo(DateUtil.formatDate(upcomingTender.getEstimatedTenderDate()));
    assertThat(upcomingTenderView.getContactName()).isEqualTo(upcomingTender.getName());
    assertThat(upcomingTenderView.getContactPhoneNumber()).isEqualTo(upcomingTender.getPhoneNumber());
    assertThat(upcomingTenderView.getContactEmailAddress()).isEqualTo(upcomingTender.getEmailAddress());
    assertThat(upcomingTenderView.getContactJobTitle()).isEqualTo(upcomingTender.getJobTitle());
    assertThat(upcomingTenderView.getProjectId()).isEqualTo(upcomingTender.getProjectDetail().getProject().getId());
    assertThat(upcomingTenderView.getDisplayOrder()).isEqualTo(displayOrder);
    assertThat(upcomingTenderView.getUploadedFileViews()).containsExactlyElementsOf(files);
    assertThat(upcomingTenderView.getAddedByOrganisationGroup()).isEqualTo(addedByPortalOrganisationGroup.getName());

    final var editSummaryLink = new SummaryLink(
        SummaryLinkText.EDIT.getDisplayName(),
        ReverseRouter.route(on(UpcomingTendersController.class).editUpcomingTender(
            upcomingTenderView.getProjectId(),
            upcomingTenderView.getId(),
            null
        ))
    );

    final var removeSummaryLink = new SummaryLink(
        SummaryLinkText.DELETE.getDisplayName(),
        ReverseRouter.route(on(UpcomingTendersController.class).removeUpcomingTenderConfirm(
            upcomingTenderView.getProjectId(),
            upcomingTenderView.getId(),
            displayOrder,
            null
        ))
    );

    if (canAccessTenderLinks) {
      assertThat(upcomingTenderView.getSummaryLinks()).containsExactly(editSummaryLink, removeSummaryLink);
    } else {
      assertThat(upcomingTenderView.getSummaryLinks()).isEmpty();
    }
  }
}