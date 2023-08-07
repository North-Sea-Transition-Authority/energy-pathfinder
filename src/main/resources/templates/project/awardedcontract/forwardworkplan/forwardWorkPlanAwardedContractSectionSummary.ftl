<#include '../../../layout.ftl'/>
<#import '../_awardedContractSummaryCommon.ftl' as awardedContractSummary>

<#-- @ftlvariable name="sectionId" type="String" -->

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
    <#if awardedContractDiffModel?has_content>
        <#list awardedContractDiffModel as awardedContractDiff>
          <@_forwardWorkPlanAwardedContractDiffSummary
            awardedContractDiff=awardedContractDiff
            showHeader=true
            showActions=false
            headingSize="h3"
            headingClass="govuk-heading-m"
          />
        </#list>
    <#else>
      <@fdsCheckAnswers.checkAnswersWrapper summaryListId="${sectionId}-setup-answers">
        <@fdsCheckAnswers.checkAnswers>
          <@checkAnswers.checkAnswersStandardOrDiffRow
            prompt="Awarded contracts to add"
            fieldValue=awardedContractSetupDiffModel.ForwardWorkPlanAwardedContractSetupView_hasContractsToAdd
            isDiffedField=true
          />
        </@fdsCheckAnswers.checkAnswers>
      </@fdsCheckAnswers.checkAnswersWrapper>
    </#if>
</@sectionSummaryWrapper.sectionSummaryWrapper>

<#macro _forwardWorkPlanAwardedContractDiffSummary
  awardedContractDiff
  showHeader=false
  showActions=false
  headingSize=awardedContractSummary.defaultHeadingSize
  headingClass=awardedContractSummary.defaultHeadingClass
>
    <@summaryViewWrapper.summaryViewItemWrapper
      idPrefix=awardedContractSummary.idPrefix
      headingPrefix=awardedContractSummary.headingPrefix
      displayOrder=awardedContractDiff.ForwardWorkPlanAwardedContractView_displayOrder.currentValue
      isValid=true
      summaryLinkList=[]
      showHeader=showHeader
      showActions=showActions
      headingSize=headingSize
      headingClass=headingClass
      diffObject=awardedContractDiff
    >
        <@awardedContractSummary.awardedContractSummaryFields
          useDiffedField=true
          contractorName=awardedContractDiff.ForwardWorkPlanAwardedContractView_contractorName
          contractFunction=awardedContractDiff.ForwardWorkPlanAwardedContractView_contractFunction
          descriptionOfWork=awardedContractDiff.ForwardWorkPlanAwardedContractView_descriptionOfWork
          dateAwarded=awardedContractDiff.ForwardWorkPlanAwardedContractView_dateAwarded
          contractBand=awardedContractDiff.ForwardWorkPlanAwardedContractView_contractBand
          contactName=awardedContractDiff.ForwardWorkPlanAwardedContractView_contactName
          contactPhoneNumber=awardedContractDiff.ForwardWorkPlanAwardedContractView_contactPhoneNumber
          contactJobTitle=awardedContractDiff.ForwardWorkPlanAwardedContractView_contactJobTitle
          contactEmailAddress=awardedContractDiff.ForwardWorkPlanAwardedContractView_contactEmailAddress
          addedByPortalOrganisationGroup=awardedContractDiff.ForwardWorkPlanAwardedContractView_addedByPortalOrganisationGroup
        />
    </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>
