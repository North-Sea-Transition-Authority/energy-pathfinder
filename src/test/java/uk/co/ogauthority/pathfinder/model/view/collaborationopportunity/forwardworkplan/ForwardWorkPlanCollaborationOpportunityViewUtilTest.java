package uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.forwardworkplan.ForwardWorkPlanCollaborationOpportunityController;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanCollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UploadedFileUtil;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanCollaborationOpportunityViewUtilTest {

  private final int displayOrder = 1;
  private final List<UploadedFileView> fileList = List.of(UploadedFileUtil.createUploadedFileView());

  @Test
  public void createView_withoutValidParam_whenFunctionFromList_thenNoTag() {

    final var entity = getPopulatedInfrastructureCollaborationOpportunity();
    entity.setFunction(Function.HR);
    entity.setManualFunction(null);

    ForwardWorkPlanCollaborationOpportunityView view = new ForwardWorkPlanCollaborationOpportunityViewUtil.ForwardWorkPlanCollaborationOpportunityViewBuilder(
        entity,
        displayOrder,
        fileList
    )
        .includeSummaryLinks(true)
        .build();

    createViewAndAssertCommonViewProperties(view, entity);

    assertThat(view.getFunction()).isEqualTo(new StringWithTag(entity.getFunction().getDisplayName(), Tag.NONE));
    assertThat(view.isValid()).isNull();
  }

  @Test
  public void createView_withoutValidParam_whenManualFunction_thenNotFromListTag() {

    final var entity = getPopulatedInfrastructureCollaborationOpportunity();
    entity.setFunction(null);

    final var manualFunction = "manual function";
    entity.setManualFunction(manualFunction);

    ForwardWorkPlanCollaborationOpportunityView view = new ForwardWorkPlanCollaborationOpportunityViewUtil.ForwardWorkPlanCollaborationOpportunityViewBuilder(
        entity,
        displayOrder,
        fileList
    )
        .includeSummaryLinks(true)
        .build();

    createViewAndAssertCommonViewProperties(view, entity);

    assertThat(view.getFunction()).isEqualTo(new StringWithTag(entity.getManualFunction(), Tag.NOT_FROM_LIST));
    assertThat(view.isValid()).isNull();
  }

  @Test
  public void createView_withValidParam_whenFunctionFromList_thenNoTag() {

    final var entity = getPopulatedInfrastructureCollaborationOpportunity();
    entity.setFunction(Function.HR);
    entity.setManualFunction(null);

    final var isValid = false;

    ForwardWorkPlanCollaborationOpportunityView view = new ForwardWorkPlanCollaborationOpportunityViewUtil.ForwardWorkPlanCollaborationOpportunityViewBuilder(
        entity,
        displayOrder,
        fileList
    )
        .includeSummaryLinks(true)
        .isValid(isValid)
        .build();

    createViewAndAssertCommonViewProperties(view, entity);

    assertThat(view.getFunction()).isEqualTo(new StringWithTag(entity.getFunction().getDisplayName(), Tag.NONE));
    assertThat(view.isValid()).isEqualTo(isValid);
  }

  @Test
  public void createView_withValidParam_whenManualFunction_thenNotFromListTag() {

    final var entity = getPopulatedInfrastructureCollaborationOpportunity();
    entity.setFunction(null);

    final var manualFunction = "manual function";
    entity.setManualFunction(manualFunction);

    final var isValid = true;

    ForwardWorkPlanCollaborationOpportunityView view = new ForwardWorkPlanCollaborationOpportunityViewUtil.ForwardWorkPlanCollaborationOpportunityViewBuilder(
        entity,
        displayOrder,
        fileList
    )
        .includeSummaryLinks(true)
        .isValid(isValid)
        .build();

    createViewAndAssertCommonViewProperties(view, entity);

    assertThat(view.getFunction()).isEqualTo(new StringWithTag(entity.getManualFunction(), Tag.NOT_FROM_LIST));
    assertThat(view.isValid()).isEqualTo(isValid);
  }

  private void createViewAndAssertCommonViewProperties(ForwardWorkPlanCollaborationOpportunityView view,
                                                       ForwardWorkPlanCollaborationOpportunity entity) {

    final var projectId = entity.getProjectDetail().getProject().getId();

    assertThat(view.getDisplayOrder()).isEqualTo(displayOrder);
    assertThat(view.getId()).isEqualTo(entity.getId());
    assertThat(view.getProjectId()).isEqualTo(projectId);
    assertThat(view.getDescriptionOfWork()).isEqualTo(entity.getDescriptionOfWork());
    assertThat(view.getUrgentResponseNeeded()).isEqualTo(StringDisplayUtil.yesNoFromBoolean(entity.getUrgentResponseNeeded()));
    assertThat(view.getContactName()).isEqualTo(entity.getContactName());
    assertThat(view.getContactPhoneNumber()).isEqualTo(entity.getPhoneNumber());
    assertThat(view.getContactJobTitle()).isEqualTo(entity.getJobTitle());
    assertThat(view.getContactEmailAddress()).isEqualTo(entity.getEmailAddress());
    assertThat(view.getUploadedFileViews()).isEqualTo(fileList);

    final var editUrl = ReverseRouter.route(on(ForwardWorkPlanCollaborationOpportunityController.class)
        .editCollaborationOpportunity(
            projectId,
            entity.getId(),
            null
        )
    );

    final var deleteUrl = ReverseRouter.route(on(ForwardWorkPlanCollaborationOpportunityController.class)
        .removeCollaborationOpportunityConfirm(
            projectId,
            entity.getId(),
            displayOrder,
            null
        )
    );

    final SummaryLink editLink = new SummaryLink(
        SummaryLinkText.EDIT.getDisplayName(),
        editUrl
    );

    final SummaryLink removeLink = new SummaryLink(
        SummaryLinkText.DELETE.getDisplayName(),
        deleteUrl
    );


    assertThat(view.getSummaryLinks()).isEqualTo(List.of(editLink, removeLink));
  }

  private ForwardWorkPlanCollaborationOpportunity getPopulatedInfrastructureCollaborationOpportunity() {
    return ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(ProjectUtil.getProjectDetails());
  }

}