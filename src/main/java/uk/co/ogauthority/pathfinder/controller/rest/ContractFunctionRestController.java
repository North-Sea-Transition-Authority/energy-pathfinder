package uk.co.ogauthority.pathfinder.controller.rest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.ogauthority.pathfinder.model.enums.project.FunctionType;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchResult;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;

@RestController
@RequestMapping("/api/contract")
public class ContractFunctionRestController {

  private final FunctionService functionService;

  @Autowired
  public ContractFunctionRestController(FunctionService functionService) {
    this.functionService = functionService;
  }

  @GetMapping("/function")
  public RestSearchResult searchContractFunctions(@Nullable @RequestParam("term") String searchTerm) {
    return new RestSearchResult(findContractFunctionsLikeWithManualEntry(searchTerm));
  }

  /**
   * Search the Function enum displayNames for those that include searchTerm.
   * @param searchTerm Term to match against Function display names
   * @return return matching results plus manual entry
   */
  private List<RestSearchItem> findContractFunctionsLikeWithManualEntry(String searchTerm) {
    return functionService.findFunctionsLikeWithManualEntry(searchTerm, FunctionType.AWARDED_CONTRACT);
  }
}
