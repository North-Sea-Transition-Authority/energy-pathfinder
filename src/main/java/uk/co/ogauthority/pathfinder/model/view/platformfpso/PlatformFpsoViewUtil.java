package uk.co.ogauthority.pathfinder.model.view.platformfpso;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import org.apache.commons.lang3.BooleanUtils;
import uk.co.ogauthority.pathfinder.controller.project.platformsfpsos.PlatformsFpsosController;
import uk.co.ogauthority.pathfinder.model.entity.project.platformsfpsos.PlatformFpso;
import uk.co.ogauthority.pathfinder.model.enums.MeasurementUnits;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.PlatformFpsoInfrastructureType;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

public class PlatformFpsoViewUtil {
  public static final String YEAR_NOT_SET_TEXT = StringDisplayUtil.NOT_SET_TEXT;
  public static final String EARLIEST_YEAR_TEXT = "Earliest start year: %s";
  public static final String LATEST_YEAR_TEXT = "Latest completion year: %s";

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

    final var isFpso = PlatformFpsoInfrastructureType.FPSO.equals(platformFpso.getInfrastructureType());

    view.setFpso(isFpso);
    view.setInfrastructureType(platformFpso.getInfrastructureType() != null
        ? platformFpso.getInfrastructureType().getDisplayName()
        : ""
    );

    view.setPlatformFpso(platformFpso.getStructure() != null
        ? new StringWithTag(platformFpso.getStructure().getFacilityName(), Tag.NONE)
        : new StringWithTag(platformFpso.getManualStructureName(), Tag.NOT_FROM_LIST)
    );

    view.setTopsideFpsoMass(platformFpso.getTopsideFpsoMass() != null
        ? getMass(platformFpso.getTopsideFpsoMass())
        : ""
    );
    view.setTopsideRemovalEarliestYear(getYearText(platformFpso.getEarliestRemovalYear(), EARLIEST_YEAR_TEXT));
    view.setTopsideRemovalLatestYear(getYearText(platformFpso.getLatestRemovalYear(), LATEST_YEAR_TEXT));

    if (isFpso) {

      view.setFpsoType(platformFpso.getFpsoType());
      view.setFpsoDimensions(platformFpso.getFpsoDimensions());

      final var substructuresToRemove = platformFpso.getSubstructuresExpectedToBeRemoved();

      view.setSubstructuresExpectedToBeRemoved(substructuresToRemove);

      if (BooleanUtils.isTrue(substructuresToRemove)) {

        view.setSubstructureRemovalPremise(platformFpso.getSubstructureRemovalPremise() != null
            ? platformFpso.getSubstructureRemovalPremise().getDisplayName()
            : ""
        );
        view.setSubstructureRemovalMass(platformFpso.getSubstructureRemovalMass() != null
            ? getMass(platformFpso.getSubstructureRemovalMass())
            : ""
        );
        view.setSubstructureRemovalEarliestYear(
            getYearText(platformFpso.getSubStructureRemovalEarliestYear(), EARLIEST_YEAR_TEXT));
        view.setSubstructureRemovalLatestYear(
            getYearText(platformFpso.getSubStructureRemovalLatestYear(), LATEST_YEAR_TEXT));

      }
    }

    view.setFuturePlans(
        platformFpso.getFuturePlans() != null ? platformFpso.getFuturePlans().getDisplayName() : "");

    var summaryLinks = new ArrayList<SummaryLink>();
    summaryLinks.add(
        new SummaryLink(
            SummaryLinkText.EDIT.getDisplayName(),
            ReverseRouter.route(on(PlatformsFpsosController.class).editPlatformFpso(
                projectId,
                platformFpso.getId(),
                null
            ))
        )
    );

    summaryLinks.add(
        new SummaryLink(
            SummaryLinkText.DELETE.getDisplayName(),
            ReverseRouter.route(on(PlatformsFpsosController.class).removePlatformFpsoConfirm(
                projectId,
                platformFpso.getId(),
                displayOrder,
                null
            ))
        )
    );
    view.setSummaryLinks(summaryLinks);
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

  /**
   * Get the placeholder not set string or format the year provided into the earliest or latest string.
   * @param year year to format into earliestOrLatestString
   * @param earliestOrLatestString either EARLIEST_YEAR_TEXT or LATEST_YEAR_TEXT
   * @return formatted year string
   */
  public static String getYearText(String year, String earliestOrLatestString) {
    return String.format(earliestOrLatestString, year != null
      ?  year
      : YEAR_NOT_SET_TEXT
    );
  }

  public static String getMass(Integer mass) {
    return String.format("%d %s", mass, MeasurementUnits.METRIC_TONNE.getPlural());
  }

}
