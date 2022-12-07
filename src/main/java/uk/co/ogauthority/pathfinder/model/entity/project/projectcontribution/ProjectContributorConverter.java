package uk.co.ogauthority.pathfinder.model.entity.project.projectcontribution;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.AttributeConverter;
import org.apache.commons.lang3.StringUtils;

public class ProjectContributorConverter implements AttributeConverter<List<Integer>, String> {
  @Override
  public String convertToDatabaseColumn(List<Integer> attribute) {
    return null;
  }

  @Override
  public List<Integer> convertToEntityAttribute(String contributingOrganisationIds) {
    if (!StringUtils.isBlank(contributingOrganisationIds)) {
      return Arrays.stream(contributingOrganisationIds.split(", "))
          .map(Integer::parseInt)
          .collect(Collectors.toList());
    } else {
      return List.of();
    }
  }
}
