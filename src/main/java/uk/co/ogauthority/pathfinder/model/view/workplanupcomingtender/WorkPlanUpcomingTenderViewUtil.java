package uk.co.ogauthority.pathfinder.model.view.workplanupcomingtender;

import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.WorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class WorkPlanUpcomingTenderViewUtil {

  private WorkPlanUpcomingTenderViewUtil() {
    throw new IllegalStateException("UpcomingTenderViewFactory is a utility class and should not be instantiated");
  }

  public static WorkPlanUpcomingTenderView createUpcomingTenderView(WorkPlanUpcomingTender workPlanUpcomingTender,
                                                                    Integer displayOrder) {
    var projectId = workPlanUpcomingTender.getProjectDetail().getProject().getId();
    var tender = new WorkPlanUpcomingTenderView(
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

    tender.setTenderDepartment(tenderFunction);
    tender.setDescriptionOfWork(workPlanUpcomingTender.getDescriptionOfWork());
    tender.setEstimatedTenderDate(DateUtil.formatDate(workPlanUpcomingTender.getEstimatedTenderDate()));
    tender.setContractBand(
        workPlanUpcomingTender.getContractBand() != null
        ? workPlanUpcomingTender.getContractBand().getDisplayName()
            : ""
    );

    tender.setContactName(workPlanUpcomingTender.getContactName());
    tender.setContactPhoneNumber(workPlanUpcomingTender.getPhoneNumber());
    tender.setContactEmailAddress(workPlanUpcomingTender.getEmailAddress());
    tender.setContactJobTitle(workPlanUpcomingTender.getJobTitle());
    
    return tender;
  }

  public static WorkPlanUpcomingTenderView createUpcomingTenderView(WorkPlanUpcomingTender workPlanUpcomingTender,
                                                                    Integer displayOrder,
                                                                    Boolean isValid) {
    var view = createUpcomingTenderView(workPlanUpcomingTender, displayOrder);
    view.setIsValid(isValid);
    return view;
  }
}
