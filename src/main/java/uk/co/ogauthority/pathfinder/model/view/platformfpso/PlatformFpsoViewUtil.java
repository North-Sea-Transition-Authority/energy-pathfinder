package uk.co.ogauthority.pathfinder.model.view.platformfpso;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import uk.co.ogauthority.pathfinder.controller.project.platformsfpsos.PlatformsFpsosController;
import uk.co.ogauthority.pathfinder.model.entity.project.platformsfpsos.PlatformFpso;
import uk.co.ogauthority.pathfinder.model.enums.MeasurementUnits;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

public class PlatformFpsoViewUtil {

  private PlatformFpsoViewUtil() {
    throw new IllegalStateException("PlatformFpsoViewUtil is a utility class and should not be instantiated.");
  }

  public static PlatformFpsoView createView(
      PlatformFpso platformFpso,
      Integer displayOrder,
      Integer projectId
  ) {
    var view = new PlatformFpsoView(
        platformFpso.getId(),
        displayOrder,
        projectId
    );

    view.setPlatformFpso(platformFpso.getStructure() != null
        ? platformFpso.getStructure().getFacilityName()
        : platformFpso.getManualStructureName()
    );

    view.setTopsideFpsoMass(platformFpso.getTopsideFpsoMass() != null
        ? getMass(platformFpso.getTopsideFpsoMass())
        : ""
    );
    view.setTopsideRemovalYears(
        getYears(platformFpso.getEarliestRemovalYear(), platformFpso.getLatestRemovalYear())
    );
    view.setSubstructuresExpectedToBeRemoved(platformFpso.getSubstructuresExpectedToBeRemoved());
    view.setSubstructureRemovalPremise(platformFpso.getSubstructureRemovalPremise() != null
        ? platformFpso.getSubstructureRemovalPremise().getDisplayName()
        : ""
    );
    view.setSubstructureRemovalMass(platformFpso.getSubstructureRemovalMass() != null
        ? getMass(platformFpso.getSubstructureRemovalMass())
        : ""
    );
    view.setSubstructureRemovalYears(getYears(
        platformFpso.getSubStructureRemovalEarliestYear(),
        platformFpso.getSubStructureRemovalLatestYear()
      )
    );
    view.setFpsoType(platformFpso.getFpsoType());
    view.setFpsoDimensions(platformFpso.getFpsoDimensions());
    view.setFuturePlans(platformFpso.getFuturePlans() != null ? platformFpso.getFuturePlans().getDisplayName() : "");
    view.setEditLink(
        new SummaryLink(
            SummaryLinkText.EDIT.getDisplayName(),
            ReverseRouter.route(on(PlatformsFpsosController.class).editPlatformFpso(
                projectId,
                platformFpso.getId(),
                null
            ))
        )
    );

    view.setDeleteLink(
        new SummaryLink(
            SummaryLinkText.DELETE.getDisplayName(),
            ReverseRouter.route(on(PlatformsFpsosController.class).deletePlatformFpsoConfirm(
                projectId,
                platformFpso.getId(),
                displayOrder,
                null
            ))
        )
    );
    return view;
  }

  public static PlatformFpsoView createView(
      PlatformFpso platformFpso,
      Integer displayOrder,
      Integer projectId,
      Boolean isValid
  ) {
    var view = createView(platformFpso, displayOrder, projectId);
    view.setIsValid(isValid);
    return view;
  }

  public static String getYears(String minYear, String maxYear) {
    var min = minYear != null ? minYear : "Not set";
    var max = maxYear != null ? maxYear : "Not set";
    return String.format("Earliest start year: %s / Latest completion year: %s", min, max);
  }

  public static String getMass(Integer mass) {
    return String.format("%d %s", mass, MeasurementUnits.METRIC_TONNE.getPlural());
  }

}
