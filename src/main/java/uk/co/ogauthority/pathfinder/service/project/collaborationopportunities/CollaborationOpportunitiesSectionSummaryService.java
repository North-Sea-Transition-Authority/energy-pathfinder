package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;
import uk.co.ogauthority.pathfinder.service.difference.DifferenceService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSectionSummaryCommonModelService;

@Service
public abstract class CollaborationOpportunitiesSectionSummaryService<V> {

  private final ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService;
  private final DifferenceService differenceService;

  @Autowired
  public CollaborationOpportunitiesSectionSummaryService(
      ProjectSectionSummaryCommonModelService projectSectionSummaryCommonModelService,
      DifferenceService differenceService
  ) {
    this.projectSectionSummaryCommonModelService = projectSectionSummaryCommonModelService;
    this.differenceService = differenceService;
  }

  /**
   * Method to get the collaboration opportunity views for the current version of the project detail.
   * @param projectDetail the project detail to get the collaboration opportunity views for
   * @return a list of collaboration opportunity views of the implementing class
   */
  protected abstract List<V> getCurrentCollaborationOpportunityViews(ProjectDetail projectDetail);

  /**
   * Method to get the collaboration opportunity views for the previous version of the project detail.
   * @param projectDetail the project detail to get the collaboration opportunity views for
   * @return a list of collaboration opportunity views of the implementing class
   */
  protected abstract List<V> getPreviousCollaborationOpportunityViews(ProjectDetail projectDetail);

  protected abstract String getTemplatePath();

  protected abstract String getPageName();

  protected abstract String getSectionId();

  protected abstract Integer getDisplayOrder();

  protected abstract SidebarSectionLink getSectionLink();

  protected abstract Integer getViewDisplayOrder(V view);

  protected abstract List<UploadedFileView> getUploadedFileViews(V view);

  protected Map<String, Object> getSummaryModel(ProjectDetail projectDetail,
                                                Class<V> viewClass) {

    final var summaryModel = projectSectionSummaryCommonModelService.getCommonSummaryModelMap(
        projectDetail,
        getPageName(),
        getSectionId()
    );

    final var currentCollaborationOpportunityViews = getCurrentCollaborationOpportunityViews(projectDetail);
    final var previousCollaborationOpportunityViews = getPreviousCollaborationOpportunityViews(projectDetail);

    List<Map<String, ?>> collaborationOpportunitiesDiffList = new ArrayList<>();

    currentCollaborationOpportunityViews.forEach(collaborationOpportunityView -> {

      var collaborationOpportunityModel = new HashMap<String, Object>();

      var previousCollaborationOpportunityView = previousCollaborationOpportunityViews
          .stream()
          .filter(view -> getViewDisplayOrder(view).equals(getViewDisplayOrder(collaborationOpportunityView)))
          .findFirst()
          .orElse(createNewViewInstantiation(viewClass));

      var collaborationOpportunityDiffModel = differenceService.differentiate(
          collaborationOpportunityView,
          previousCollaborationOpportunityView,
          Set.of("summaryLinks", "uploadedFileViews")
      );

      var uploadedFileDiffModel = differenceService.differentiateComplexLists(
          getUploadedFileViews(collaborationOpportunityView),
          getUploadedFileViews(previousCollaborationOpportunityView),
          Set.of("fileUploadedTime"),
          Set.of("fileUrl"),
          UploadedFileView::getFileId,
          UploadedFileView::getFileId
      );

      collaborationOpportunityModel.put("collaborationOpportunityDiff", collaborationOpportunityDiffModel);
      collaborationOpportunityModel.put("collaborationOpportunityFiles", uploadedFileDiffModel);

      collaborationOpportunitiesDiffList.add(collaborationOpportunityModel);

    });

    summaryModel.put("collaborationOpportunityDiffModel", collaborationOpportunitiesDiffList);

    return summaryModel;
  }

  protected ProjectSectionSummary getProjectSectionSummary(Map<String, Object> summaryModel) {
    return new ProjectSectionSummary(
        List.of(getSectionLink()),
        getTemplatePath(),
        summaryModel,
        getDisplayOrder()
    );
  }

  private V createNewViewInstantiation(Class<V> viewClass) {
    try {
      return viewClass.getConstructor().newInstance();
    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
      throw new RuntimeException(
          String.format(
              "Could not construct new instance of class %s as part of getSummaryModel",
              viewClass.getName()
          ),
          ex
      );
    }
  }
}
