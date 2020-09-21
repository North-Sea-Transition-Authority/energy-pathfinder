package uk.co.ogauthority.pathfinder.service.project.awardedcontract;

import java.util.ArrayList;
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
import uk.co.ogauthority.pathfinder.util.summary.SummaryUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Service
public class AwardedContractSummaryService {

  public static final String ERROR_FIELD_NAME = "awarded-contract-%d";
  public static final String ERROR_MESSAGE = "Awarded contract %d is incomplete";
  public static final String EMPTY_LIST_ERROR = "You must add at least one awarded contract";

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
    return SummaryUtil.getErrors(new ArrayList<>(awardedContractViews), EMPTY_LIST_ERROR, ERROR_FIELD_NAME, ERROR_MESSAGE);
  }

  public ValidationResult validateViews(List<AwardedContractView> views) {
    return SummaryUtil.validateViews(new ArrayList<>(views));
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
