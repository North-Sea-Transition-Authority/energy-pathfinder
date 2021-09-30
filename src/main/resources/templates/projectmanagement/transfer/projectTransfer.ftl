<#include '../../layout.ftl'>

<#-- @ftlvariable name="projectTransferView" type="uk.co.ogauthority.pathfinder.model.view.projecttransfer.ProjectTransferView" -->
<#-- @ftlvariable name="isPublishedAsOperator" type="Boolean" -->

<#if projectTransferView?has_content>
  <h2 class="govuk-heading-l">Project operator change details</h2>
  <@fdsCheckAnswers.checkAnswers>
    <@checkAnswers.checkAnswersRowNoActions prompt="Previous operator" value=projectTransferView.oldOperator />
    <@checkAnswers.checkAnswersRowNoActions prompt="New operator" value=projectTransferView.newOperator />
    <@checkAnswers.checkAnswersRowNoActions prompt="Reason for operator change" value=projectTransferView.transferReason />
    <#if projectTransferView.isPublishedAsOperator?has_content>
      <@checkAnswers.checkAnswersRowNoActions
        prompt="Show project operator on supply chain interface"
        value=projectTransferView.isPublishedAsOperator
      />
    </#if>
    <#if !isPublishedAsOperator && projectTransferView.publishableOrganisationName?has_content>
      <@checkAnswers.checkAnswersRowNoActions
        prompt="Operator to show on supply chain interface"
        value=projectTransferView.publishableOrganisationName
      />
    </#if>
    <@checkAnswers.checkAnswersRowNoActions prompt="Operator change date" value=projectTransferView.transferDate />
    <@checkAnswers.checkAnswersRowNoActionsWithNested prompt="Operator changed by">
      <div>${projectTransferView.transferredByUserName}</div>
      <div>${projectTransferView.transferredByUserEmailAddress}</div>
    </@checkAnswers.checkAnswersRowNoActionsWithNested>
  </@fdsCheckAnswers.checkAnswers>
</#if>
