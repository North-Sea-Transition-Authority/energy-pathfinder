package uk.co.ogauthority.pathfinder.model.view.subseainfrastructure;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import uk.co.ogauthority.pathfinder.controller.project.subseainfrastructure.SubseaInfrastructureController;
import uk.co.ogauthority.pathfinder.model.entity.project.subseainfrastructure.SubseaInfrastructure;
import uk.co.ogauthority.pathfinder.model.enums.MeasurementUnits;
import uk.co.ogauthority.pathfinder.model.enums.project.subseainfrastructure.SubseaInfrastructureType;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

public class SubseaInfrastructureViewUtil {

  public static final String DEFAULT_DECOM_YEAR_TEXT = StringDisplayUtil.NOT_SET_TEXT;
  public static final String EARLIEST_DECOM_YEAR_TEXT = "Earliest start year: %s";
  public static final String LATEST_DECOM_YEAR_TEXT = "Latest completion year: %s";

  private SubseaInfrastructureViewUtil() {
    throw new IllegalStateException("SubseaInfrastructureViewUtil is a util class and should not be instantiated");
  }

  public static SubseaInfrastructureView from(SubseaInfrastructure subseaInfrastructure, Integer displayOrder) {
    return from(subseaInfrastructure, displayOrder, true);
  }

  public static SubseaInfrastructureView from(SubseaInfrastructure subseaInfrastructure,
                                              Integer displayOrder,
                                              boolean isValid) {
    var subseaInfrastructureView = new SubseaInfrastructureView();
    subseaInfrastructureView.setDisplayOrder(displayOrder);
    subseaInfrastructureView.setId(subseaInfrastructure.getId());

    var projectId = subseaInfrastructure.getProjectDetail().getProject().getId();
    subseaInfrastructureView.setProjectId(projectId);

    var structure = (subseaInfrastructure.getFacility() != null)
        ? new StringWithTag(subseaInfrastructure.getFacility().getSelectionText(), Tag.NONE)
        : new StringWithTag(subseaInfrastructure.getManualFacility(), Tag.NOT_FROM_LIST);
    subseaInfrastructureView.setStructure(structure);

    subseaInfrastructureView.setDescription(subseaInfrastructure.getDescription());

    var status = (subseaInfrastructure.getStatus() != null)
        ? subseaInfrastructure.getStatus().getDisplayName()
        : null;
    subseaInfrastructureView.setStatus(status);

    var subseaInfrastructureType = subseaInfrastructure.getInfrastructureType();

    var infrastructureTypeDisplayValue = (subseaInfrastructureType != null)
        ? subseaInfrastructureType.getDisplayName()
        : null;
    subseaInfrastructureView.setInfrastructureType(infrastructureTypeDisplayValue);

    if (subseaInfrastructureType == null) {
      subseaInfrastructureView.setNumberOfMattresses(null);
      subseaInfrastructureView.setTotalEstimatedMattressMass(null);
      subseaInfrastructureView.setTotalEstimatedSubseaMass(null);
      subseaInfrastructureView.setOtherInfrastructureType(null);
      subseaInfrastructureView.setTotalEstimatedOtherMass(null);
    } else if (subseaInfrastructureType.equals(SubseaInfrastructureType.CONCRETE_MATTRESSES)) {
      setConcreteMattressViewFields(subseaInfrastructure, subseaInfrastructureView);
    } else if (subseaInfrastructureType.equals(SubseaInfrastructureType.SUBSEA_STRUCTURE)) {
      setSubseaStructureViewFields(subseaInfrastructure, subseaInfrastructureView);
    } else if (subseaInfrastructureType.equals(SubseaInfrastructureType.OTHER)) {
      setOtherInfrastructureViewFields(subseaInfrastructure, subseaInfrastructureView);
    }

    setDecommissioningPeriod(subseaInfrastructure, subseaInfrastructureView);

    var summaryLinks = new ArrayList<SummaryLink>();
    summaryLinks.add(getEditSummaryLink(projectId, subseaInfrastructure.getId()));
    summaryLinks.add(getDeleteSummaryLink(projectId, subseaInfrastructure.getId(), displayOrder));
    subseaInfrastructureView.setSummaryLinks(summaryLinks);

    subseaInfrastructureView.setIsValid(isValid);

    return subseaInfrastructureView;
  }

  private static void setDecommissioningPeriod(SubseaInfrastructure subseaInfrastructure,
                                               SubseaInfrastructureView subseaInfrastructureView) {

    var decomStart = (subseaInfrastructure.getEarliestDecommissioningStartYear() != null)
        ? String.valueOf(subseaInfrastructure.getEarliestDecommissioningStartYear())
        : DEFAULT_DECOM_YEAR_TEXT;

    var decomFinish = (subseaInfrastructure.getLatestDecommissioningCompletionYear() != null)
        ? String.valueOf(subseaInfrastructure.getLatestDecommissioningCompletionYear())
        : DEFAULT_DECOM_YEAR_TEXT;

    subseaInfrastructureView.setEarliestDecommissioningStartYear(String.format(EARLIEST_DECOM_YEAR_TEXT, decomStart));
    subseaInfrastructureView.setLatestDecommissioningCompletionYear(String.format(LATEST_DECOM_YEAR_TEXT, decomFinish));
  }

  private static void setConcreteMattressViewFields(SubseaInfrastructure subseaInfrastructure,
                                                    SubseaInfrastructureView subseaInfrastructureView) {
    subseaInfrastructureView.setConcreteMattress(true);
    subseaInfrastructureView.setNumberOfMattresses(subseaInfrastructure.getNumberOfMattresses());
    subseaInfrastructureView.setTotalEstimatedMattressMass(subseaInfrastructure.getTotalEstimatedMattressMass() != null
        ? getMassString(subseaInfrastructure.getTotalEstimatedMattressMass())
        : ""
    );
  }

  private static void setSubseaStructureViewFields(SubseaInfrastructure subseaInfrastructure,
                                                   SubseaInfrastructureView subseaInfrastructureView) {
    subseaInfrastructureView.setSubseaStructure(true);
    var subseaMass = (subseaInfrastructure.getTotalEstimatedSubseaMass() != null)
        ? subseaInfrastructure.getTotalEstimatedSubseaMass().getDisplayName()
        : null;
    subseaInfrastructureView.setTotalEstimatedSubseaMass(subseaMass);
  }

  private static void setOtherInfrastructureViewFields(SubseaInfrastructure subseaInfrastructure,
                                                       SubseaInfrastructureView subseaInfrastructureView) {
    subseaInfrastructureView.setOtherInfrastructure(true);
    subseaInfrastructureView.setOtherInfrastructureType(subseaInfrastructure.getOtherInfrastructureType());
    subseaInfrastructureView.setTotalEstimatedOtherMass(subseaInfrastructure.getTotalEstimatedOtherMass() != null
        ? getMassString(subseaInfrastructure.getTotalEstimatedOtherMass())
        : ""
    );
  }

  private static SummaryLink getEditSummaryLink(Integer projectId, Integer subseaInfrastructureId) {
    return new SummaryLink(
        SummaryLinkText.EDIT.getDisplayName(),
        ReverseRouter.route(on(SubseaInfrastructureController.class).getSubseaInfrastructure(
            projectId,
            subseaInfrastructureId,
            null
        ))
    );
  }

  private static SummaryLink getDeleteSummaryLink(Integer projectId,
                                                  Integer subseaInfrastructureId,
                                                  Integer displayOrder) {
    return new SummaryLink(
        SummaryLinkText.DELETE.getDisplayName(),
        ReverseRouter.route(on(SubseaInfrastructureController.class).removeSubseaInfrastructuresConfirmation(
            projectId,
            subseaInfrastructureId,
            displayOrder,
            null
        ))
    );
  }

  public static String getMassString(Integer mass) {
    return String.format("%d %s", mass, MeasurementUnits.METRIC_TONNE.getPlural());
  }
}
