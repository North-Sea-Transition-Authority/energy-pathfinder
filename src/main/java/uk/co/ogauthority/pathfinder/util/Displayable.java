package uk.co.ogauthority.pathfinder.util;

import uk.co.fivium.digitalenummaterialisationlibrary.enummaterialisation.MaterialisableEnum;

public interface Displayable extends MaterialisableEnum {

  default String getDisplayName() {
    return "";
  }
}
