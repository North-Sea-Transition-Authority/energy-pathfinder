package uk.co.ogauthority.pathfinder.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchResult;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractService;

@RestController
@RequestMapping("/api/contract")
public class ContractFunctionRestController {

  private final AwardedContractService awardedContractService;

  @Autowired
  public ContractFunctionRestController(AwardedContractService awardedContractService) {
    this.awardedContractService = awardedContractService;
  }

  @GetMapping("/function")
  public RestSearchResult searchContractFunctions(@Nullable @RequestParam("term") String searchTerm) {
    return new RestSearchResult(awardedContractService.findContractFunctionsLikeWithManualEntry(searchTerm));
  }
}
