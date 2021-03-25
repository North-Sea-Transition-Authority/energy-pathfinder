package uk.co.ogauthority.pathfinder.model.enums.project;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectable;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum Function implements SearchSelectable {
  LOGISTICS("Logistics", Set.of(FunctionType.values())),
  FACILITIES_ONSHORE("Facilities (onshore)", Set.of(FunctionType.values())),
  FACILITIES_OFFSHORE("Facilities (offshore)", Set.of(FunctionType.values())),
  ENGINEERING_AND_DESIGN("Engineering and design", Set.of(FunctionType.values())),
  OPERATIONS_AND_MAINTENANCE("Operations and Maintenance (O&M)", Set.of(FunctionType.values())),
  SUBSURFACE("Subsurface", Set.of(FunctionType.values())),
  SUBSEA("Subsea", Set.of(FunctionType.values())),
  IT("Information Technology (IT)", Set.of(FunctionType.values())),
  PROJECTS("Projects", Set.of(FunctionType.values())),
  HSEQ("Health and Safety, Environmental and Quality (HSEQ)", Set.of(FunctionType.values())),
  DRILLING("Drilling", Set.of(FunctionType.values())),
  FABRICATION("Fabrication", Set.of(FunctionType.values())),
  HR("Human Resources (HR)", Set.of(FunctionType.values()));

  private final String displayName;

  private final Set<FunctionType> functionTypes;

  Function(String displayName, Set<FunctionType> functionTypes) {
    this.displayName = displayName;
    this.functionTypes = functionTypes;
  }

  public String getDisplayName() {
    return displayName;
  }

  public Set<FunctionType> getFunctionTypes() {
    return functionTypes;
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, Function::getDisplayName));
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
