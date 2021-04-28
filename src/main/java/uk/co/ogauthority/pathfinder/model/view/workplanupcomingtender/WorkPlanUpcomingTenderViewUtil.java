package uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender.WorkPlanUpcomingTenderController;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.WorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.duration.DurationPeriod;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class WorkPlanUpcomingTenderViewUtil {

  private WorkPlanUpcomingTenderViewUtil() {
    throw new IllegalStateException("WorkPlanUpcomingTenderViewUtil is a utility class and should not be instantiated");
  }

  public static WorkPlanUpcomingTenderView createUpcomingTenderView(WorkPlanUpcomingTender workPlanUpcomingTender,
                                                                    Integer displayOrder) {
    var projectId = workPlanUpcomingTender.getProjectDetail().getProject().getId();
    var tenderView = new WorkPlanUpcomingTenderView(
        displayOrder,
        workPlanUpcomingTender.getId(),
        projectId
    );

    var tenderFunction = new StringWithTag();

    if (workPlanUpcomingTender.getDepartmentType() != null) {
      tenderFunction = new StringWithTag(workPlanUpcomingTender.getDepartmentType().getDisplayName(), Tag.NONE);
    } else if (workPlanUpcomingTender.getManualDepartmentType() != null) {
      tenderFunction = new StringWithTag(workPlanUpcomingTender.getManualDepartmentType(), Tag.NOT_FROM_LIST);
    }

    tenderView.setTenderDepartment(tenderFunction);
    tenderView.setDescriptionOfWork(workPlanUpcomingTender.getDescriptionOfWork());
    tenderView.setEstimatedTenderDate(DateUtil.formatDate(workPlanUpcomingTender.getEstimatedTenderDate()));
    tenderView.setContractBand(
        workPlanUpcomingTender.getContractBand() != null
            ? workPlanUpcomingTender.getContractBand().getDisplayName()
            : ""
    );

    tenderView.setContactName(workPlanUpcomingTender.getContactName());
    tenderView.setContactPhoneNumber(workPlanUpcomingTender.getPhoneNumber());
    tenderView.setContactEmailAddress(workPlanUpcomingTender.getEmailAddress());
    tenderView.setContactJobTitle(workPlanUpcomingTender.getJobTitle());

    var contractLength = getContractLength(
        workPlanUpcomingTender.getContractTermDurationPeriod(),
        workPlanUpcomingTender.getContractTermDuration()
    );

    tenderView.setContractLength(contractLength);

    var editLink = new SummaryLink(
        SummaryLinkText.EDIT.getDisplayName(),
        ReverseRouter.route(on(WorkPlanUpcomingTenderController.class).editUpcomingTender(
            projectId,
            workPlanUpcomingTender.getId(),
            null
        ))
    );

    tenderView.setSummaryLinks(List.of(editLink));

    return tenderView;
  }

  public static WorkPlanUpcomingTenderView createUpcomingTenderView(WorkPlanUpcomingTender workPlanUpcomingTender,
                                                                    Integer displayOrder,
                                                                    Boolean isValid) {
    var view = createUpcomingTenderView(workPlanUpcomingTender, displayOrder);
    view.setIsValid(isValid);
    return view;
  }

  private static String getContractLength(DurationPeriod contractTermDurationPeriod,
                                          Integer contractTermDuration) {

    var contractLength = "";

    if (contractTermDuration != null && contractTermDurationPeriod != null) {
      contractLength = String.format(
          "%s %s",
          contractTermDuration,
          (contractTermDuration == 1)
              ? contractTermDurationPeriod.getDisplayNameSingular().toLowerCase()
              : contractTermDurationPeriod.getDisplayNamePlural().toLowerCase()
      );
    }

    return contractLength;
  }
}
