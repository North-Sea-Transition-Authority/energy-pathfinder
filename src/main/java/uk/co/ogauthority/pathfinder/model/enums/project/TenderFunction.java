package uk.co.ogauthority.pathfinder.model.enums.project;

import java.util.Arrays;
import java.util.Map;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectable;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum TenderFunction implements SearchSelectable {
  LOGISTICS("Logistics"),
  FACILITIES_ONSHORE("Facilities (onshore)"),
  FACILITIES_OFFSHORE("Facilities (offshore)"),
  ENGINEERING_AND_DESIGN("Engineering and design"),
  O_AND_M("O&M"),
  SUBSURFACE("Subsurface"),
  SUBSEA("Subsea"),
  IT("IT"),
  PROJECTS("Projects"),
  HSEQ("HSEQ"),
  DRILLING("Drilling"),
  FABRICATION("Fabrication"),
  HR("HR");

  private final String displayName;

  TenderFunction(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, TenderFunction::getDisplayName));
  }

  @Override
  public String getSelectionId() {
    return name();
  }

  @Override
  public String getSelectionText() {
    return getDisplayName();
  }
}
