package uk.co.ogauthority.pathfinder.service.project.upcomingtender;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem;
import uk.co.ogauthority.pathfinder.model.view.upcomingtender.UpcomingTenderView;
import uk.co.ogauthority.pathfinder.model.view.upcomingtender.UpcomingTenderViewUtil;
import uk.co.ogauthority.pathfinder.util.summary.SummaryUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Service
public class UpcomingTenderSummaryService {
  public static final String ERROR_FIELD_NAME = "upcoming-tender-%d";
  public static final String ERROR_MESSAGE = "Upcoming tender %d is incomplete";
  public static final String EMPTY_LIST_ERROR = "You must add at least one upcoming tender";

  private final UpcomingTenderService upcomingTenderService;
  private final UpcomingTenderFileLinkService upcomingTenderFileLinkService;

  @Autowired
  public UpcomingTenderSummaryService(UpcomingTenderService upcomingTenderService,
                                      UpcomingTenderFileLinkService upcomingTenderFileLinkService) {
    this.upcomingTenderService = upcomingTenderService;
    this.upcomingTenderFileLinkService = upcomingTenderFileLinkService;
  }

  public List<UpcomingTenderView> getSummaryViews(ProjectDetail detail) {
    return createUpcomingTenderViews(
        upcomingTenderService.getUpcomingTendersForDetail(detail),
        ValidationType.NO_VALIDATION
    );
  }

  public List<UpcomingTenderView> getSummaryViews(Project project, Integer version) {
    return createUpcomingTenderViews(
        upcomingTenderService.getUpcomingTendersForProjectVersion(project, version),
        ValidationType.NO_VALIDATION
    );
  }

  public List<UpcomingTenderView> getValidatedSummaryViews(ProjectDetail detail) {
    return createUpcomingTenderViews(
        upcomingTenderService.getUpcomingTendersForDetail(detail),
        ValidationType.FULL
    );
  }

  public UpcomingTenderView getUpcomingTenderView(UpcomingTender upcomingTender, Integer displayOrder) {
    var uploadedFileViews = upcomingTenderFileLinkService.getFileUploadViewsLinkedToUpcomingTender(
        upcomingTender
    );
    return UpcomingTenderViewUtil.createUpComingTenderView(upcomingTender, displayOrder, uploadedFileViews);
  }

  private UpcomingTenderView getUpcomingTenderView(UpcomingTender upcomingTender,
                                                  Integer displayOrder,
                                                  boolean isValid) {
    var uploadedFileViews = upcomingTenderFileLinkService.getFileUploadViewsLinkedToUpcomingTender(
        upcomingTender
    );
    return UpcomingTenderViewUtil.createUpComingTenderView(upcomingTender, displayOrder, uploadedFileViews, isValid);
  }

  public List<ErrorItem> getErrors(List<UpcomingTenderView> views) {
    return SummaryUtil.getErrors(new ArrayList<>(views), EMPTY_LIST_ERROR, ERROR_FIELD_NAME, ERROR_MESSAGE);
  }

  public ValidationResult validateViews(List<UpcomingTenderView> views) {
    return SummaryUtil.validateViews(new ArrayList<>(views));
  }

  /**
   * Create views for each tender in the list, validate depending on validation type.
   * @param upcomingTenders List of Tenders
   * @param validationType ValidationType (generally expected FULL or NO_VALIDATION)
   * @return A list of views validated if necessary.
   */
  public List<UpcomingTenderView> createUpcomingTenderViews(List<UpcomingTender> upcomingTenders, ValidationType validationType) {
    List<UpcomingTenderView> views = new ArrayList<>();
    for (int i = 0; i < upcomingTenders.size(); i++) {
      var displayOrder = i + 1;

      var upcomingTender = upcomingTenders.get(i);

      views.add(validationType.equals(ValidationType.NO_VALIDATION)
          ? getUpcomingTenderView(upcomingTender, displayOrder)
          : getUpcomingTenderView(
              upcomingTender,
              displayOrder,
              upcomingTenderService.isValid(upcomingTender, validationType)
          )
      );
    }
    return views;
  }

  public boolean isTaskValidForProjectDetail(ProjectDetail detail) {
    return upcomingTenderService.isTaskValidForProjectDetail(detail);
  }
}
