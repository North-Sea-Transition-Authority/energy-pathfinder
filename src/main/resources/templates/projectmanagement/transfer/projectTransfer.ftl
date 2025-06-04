<#include '../../layout.ftl'>

<#-- @ftlvariable name="projectTransferView" type="uk.co.ogauthority.pathfinder.model.view.projecttransfer.ProjectTransferView" -->
<#-- @ftlvariable name="isPublishedAsOperator" type="Boolean" -->

<#if projectTransferView?has_content>
  <h2 class="govuk-heading-l">Operator/Developer change details</h2>
  <@fdsCheckAnswers.checkAnswers>
    <@checkAnswers.checkAnswersRowNoActions prompt="Previous operator/developer" value=projectTransferView.oldOperator />
    <@checkAnswers.checkAnswersRowNoActions prompt="New operator/developer" value=projectTransferView.newOperator />
    <@checkAnswers.checkAnswersRowNoActions prompt="Reason for operator/developer change" value=projectTransferView.transferReason />
    <#if projectTransferView.isPublishedAsOperator?has_content>
      <@checkAnswers.checkAnswersRowNoActions
        prompt="Show project operator/developer on supply chain interface"
        value=projectTransferView.isPublishedAsOperator
      />
    </#if>
    <#if !isPublishedAsOperator && projectTransferView.publishableOrganisationName?has_content>
      <@checkAnswers.checkAnswersRowNoActions
        prompt="Operator/Developer to show on supply chain interface"
        value=projectTransferView.publishableOrganisationName
      />
    </#if>
    <@checkAnswers.checkAnswersRowNoActions prompt="Operator/Developer change date" value=projectTransferView.transferDate />
    <@checkAnswers.checkAnswersRowNoActionsWithNested prompt="Operator/Developer changed by">
      <div>${projectTransferView.transferredByUserName}</div>
      <div><@mailTo.mailToLink mailToEmailAddress=projectTransferView.transferredByUserEmailAddress/></div>
    </@checkAnswers.checkAnswersRowNoActionsWithNested>
  </@fdsCheckAnswers.checkAnswers>
</#if>
