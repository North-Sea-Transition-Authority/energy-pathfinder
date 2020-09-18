package uk.co.ogauthority.pathfinder.service.project.awardedcontract;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.AwardedContractView;
import uk.co.ogauthority.pathfinder.model.view.awardedcontract.AwardedContractViewUtil;

@Service
public class AwardedContractSummaryService {

  public static final String ERROR_FIELD_NAME = "awarded-contract-%d";
  public static final String ERROR_MESSAGE = "Awarded contract %d is incomplete";

  private final AwardedContractService awardedContractService;

  @Autowired
  public AwardedContractSummaryService(AwardedContractService awardedContractService) {
    this.awardedContractService = awardedContractService;
  }

  public List<AwardedContractView> getAwardedContractViews(ProjectDetail projectDetail) {
    return constructAwardedContractViews(projectDetail, ValidationType.NO_VALIDATION);
  }

  public List<AwardedContractView> getValidatedAwardedContractViews(ProjectDetail projectDetail) {
    return constructAwardedContractViews(projectDetail, ValidationType.FULL);
  }

  public AwardedContractView getAwardedContractView(Integer awardedContractId,
                                                    ProjectDetail projectDetail,
                                                    Integer displayOrder) {
    var awardedContract = awardedContractService.getAwardedContract(awardedContractId, projectDetail);
    return AwardedContractViewUtil.from(awardedContract, displayOrder);
  }

  public List<ErrorItem> getAwardedContractViewErrors(List<AwardedContractView> awardedContractViews) {
    return awardedContractViews
        .stream()
        .filter(awardedContractView -> !awardedContractView.isValid())
        .map(awardedContractView ->
            new ErrorItem(
                awardedContractView.getDisplayOrder(),
                String.format(ERROR_FIELD_NAME, awardedContractView.getDisplayOrder()),
                String.format(ERROR_MESSAGE, awardedContractView.getDisplayOrder())
            )
        )
        .collect(Collectors.toList());
  }

  public boolean areAllAwardedContractsValid(List<AwardedContractView> awardedContractViews) {
    return awardedContractViews
        .stream()
        .allMatch(AwardedContractView::isValid);
  }

  private List<AwardedContractView> constructAwardedContractViews(ProjectDetail projectDetail,
                                                                  ValidationType validationType) {
    var awardedContracts = awardedContractService.getAwardedContracts(projectDetail);
    return IntStream.range(0, awardedContracts.size())
        .mapToObj(index -> {

          AwardedContractView awardedContractView;
          var awardedContract = awardedContracts.get(index);
          var displayIndex = index + 1;

          if (validationType.equals(ValidationType.NO_VALIDATION)) {
            awardedContractView = AwardedContractViewUtil.from(awardedContract, displayIndex);
          } else {
            var isValid = awardedContractService.isValid(awardedContract, validationType);
            awardedContractView = AwardedContractViewUtil.from(awardedContract, displayIndex, isValid);
          }

          return awardedContractView;

        })
        .collect(Collectors.toList());
  }

}
