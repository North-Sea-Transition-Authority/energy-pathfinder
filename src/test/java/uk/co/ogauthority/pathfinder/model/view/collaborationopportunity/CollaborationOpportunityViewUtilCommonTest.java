package uk.co.ogauthority.pathfinder.model.view.collaborationopportunity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UploadedFileUtil;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

@RunWith(MockitoJUnitRunner.class)
public class CollaborationOpportunityViewUtilCommonTest {

  private final int displayOrder = 1;
  private final List<UploadedFileView> fileList = List.of(UploadedFileUtil.createUploadedFileView());
  private final PortalOrganisationGroup addedByPortalOrganisationGroup =
      TeamTestingUtil.generateOrganisationGroup(1, "org", "org");

  private final String editUrl = "editUrl";

  private final SummaryLink editLink = new SummaryLink(
      SummaryLinkText.EDIT.getDisplayName(),
      editUrl
  );

  private final String deleteUrl = "deleteUrl";

  private final SummaryLink removeLink = new SummaryLink(
      SummaryLinkText.DELETE.getDisplayName(),
      deleteUrl
  );

  @Test
  public void populateView_withoutValidParam_whenFunctionFromList_thenNoTag() {

    final var entity = getPopulatedTestCollaborationOpportunityCommon();
    entity.setFunction(Function.HR);
    entity.setManualFunction(null);

    final var view = new TestCollaborationOpportunityViewCommon();

    CollaborationOpportunityViewUtilCommon.populateView(
        view,
        entity,
        true,
        displayOrder,
        fileList,
        editUrl,
        deleteUrl,
        addedByPortalOrganisationGroup
    );

    populateViewAndAssertCommonViewProperties(view, entity);

    assertThat(view.getFunction()).isEqualTo(new StringWithTag(entity.getFunction().getDisplayName(), Tag.NONE));
    assertThat(view.isValid()).isNull();
  }

  @Test
  public void populateView_withoutValidParam_whenManualFunction_thenNotFromListTag() {

    final var entity = getPopulatedTestCollaborationOpportunityCommon();
    entity.setFunction(null);

    final var manualFunction = "manual function";
    entity.setManualFunction(manualFunction);

    final var view = new TestCollaborationOpportunityViewCommon();

    CollaborationOpportunityViewUtilCommon.populateView(
        view,
        entity,
        true,
        displayOrder,
        fileList,
        editUrl,
        deleteUrl,
        addedByPortalOrganisationGroup
    );

    populateViewAndAssertCommonViewProperties(view, entity);

    assertThat(view.getFunction()).isEqualTo(new StringWithTag(entity.getManualFunction(), Tag.NOT_FROM_LIST));
    assertThat(view.isValid()).isNull();
  }

  @Test
  public void populateView_withValidParam_whenFunctionFromList_thenNoTag() {

    final var entity = getPopulatedTestCollaborationOpportunityCommon();
    entity.setFunction(Function.HR);
    entity.setManualFunction(null);

    final var isValid = false;

    final var view = new TestCollaborationOpportunityViewCommon();

    CollaborationOpportunityViewUtilCommon.populateView(
        view,
        entity,
        true,
        displayOrder,
        fileList,
        editUrl,
        deleteUrl,
        isValid,
        addedByPortalOrganisationGroup
    );

    populateViewAndAssertCommonViewProperties(view, entity);

    assertThat(view.getFunction()).isEqualTo(new StringWithTag(entity.getFunction().getDisplayName(), Tag.NONE));
    assertThat(view.isValid()).isEqualTo(isValid);
  }

  @Test
  public void populateView_withValidParam_whenManualFunction_thenNotFromListTag() {

    final var entity = getPopulatedTestCollaborationOpportunityCommon();
    entity.setFunction(null);

    final var manualFunction = "manual function";
    entity.setManualFunction(manualFunction);

    final var isValid = true;

    final var view = new TestCollaborationOpportunityViewCommon();

    CollaborationOpportunityViewUtilCommon.populateView(
        view,
        entity,
        true,
        displayOrder,
        fileList,
        editUrl,
        deleteUrl,
        isValid,
        addedByPortalOrganisationGroup
    );

    populateViewAndAssertCommonViewProperties(view, entity);

    assertThat(view.getFunction()).isEqualTo(new StringWithTag(entity.getManualFunction(), Tag.NOT_FROM_LIST));
    assertThat(view.isValid()).isEqualTo(isValid);
  }

  @Test
  public void populateView_emptyAddedByPortalOrganisationGroup_thenDefaultAddedByString() {
    var entity = getPopulatedTestCollaborationOpportunityCommon();
    var view = new TestCollaborationOpportunityViewCommon();
    var emptyPortalOrganisationGroup = new PortalOrganisationGroup();

    CollaborationOpportunityViewUtilCommon.populateView(
        view,
        entity,
        true,
        displayOrder,
        fileList,
        editUrl,
        deleteUrl,
        emptyPortalOrganisationGroup
    );

    assertThat(view.getAddedByPortalOrganisationGroup()).isEqualTo("Unknown organisation");
  }

  private void populateViewAndAssertCommonViewProperties(TestCollaborationOpportunityViewCommon view,
                                                         TestCollaborationOpportunityCommon entity) {

    assertThat(view.getDisplayOrder()).isEqualTo(displayOrder);
    assertThat(view.getId()).isEqualTo(entity.getId());
    assertThat(view.getProjectId()).isEqualTo(entity.getProjectDetail().getProject().getId());
    assertThat(view.getDescriptionOfWork()).isEqualTo(entity.getDescriptionOfWork());
    assertThat(view.getUrgentResponseNeeded()).isEqualTo(StringDisplayUtil.yesNoFromBoolean(entity.getUrgentResponseNeeded()));
    assertThat(view.getContactName()).isEqualTo(entity.getContactName());
    assertThat(view.getContactPhoneNumber()).isEqualTo(entity.getPhoneNumber());
    assertThat(view.getContactJobTitle()).isEqualTo(entity.getJobTitle());
    assertThat(view.getContactEmailAddress()).isEqualTo(entity.getEmailAddress());
    assertThat(view.getUploadedFileViews()).isEqualTo(fileList);
    assertThat(view.getSummaryLinks()).isEqualTo(List.of(editLink, removeLink));
    assertThat(view.getAddedByPortalOrganisationGroup()).isEqualTo(addedByPortalOrganisationGroup.getName());
  }

  private TestCollaborationOpportunityCommon getPopulatedTestCollaborationOpportunityCommon() {
    final var testCollaborationOpportunityCommon = new TestCollaborationOpportunityCommon();
    testCollaborationOpportunityCommon.setProjectDetail(ProjectUtil.getProjectDetails());
    testCollaborationOpportunityCommon.setFunction(Function.HR);
    testCollaborationOpportunityCommon.setManualFunction(null);
    testCollaborationOpportunityCommon.setDescriptionOfWork("description");
    testCollaborationOpportunityCommon.setUrgentResponseNeeded(true);
    testCollaborationOpportunityCommon.setContactName("name");
    testCollaborationOpportunityCommon.setPhoneNumber("phone number");
    testCollaborationOpportunityCommon.setJobTitle("job title");
    testCollaborationOpportunityCommon.setEmailAddress("someone@example.com");
    return testCollaborationOpportunityCommon;
  }
}