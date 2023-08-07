<#include '../../../layout.ftl'/>
<#import '../_awardedContractSummaryCommon.ftl' as awardedContractSummary>

<#-- @ftlvariable name="sectionId" type="String" -->

<@sectionSummaryWrapper.sectionSummaryWrapper sectionId=sectionId sectionTitle=sectionTitle>
    <#if awardedContractDiffModel?has_content>
        <#list awardedContractDiffModel as awardedContractDiff>
            <@infrastructureAwardedContractDiffSummary
              awardedContractDiff=awardedContractDiff
              showHeader=true
              showActions=false
              headingSize="h3"
              headingClass="govuk-heading-m"
            />
        </#list>
    <#else>
        <@emptySectionSummaryInset.emptySectionSummaryInset
          itemText="awarded contracts"
          projectTypeDisplayName=projectTypeDisplayNameLowercase
        />
    </#if>
</@sectionSummaryWrapper.sectionSummaryWrapper>

<#macro infrastructureAwardedContractDiffSummary
  awardedContractDiff
  showHeader=false
  showActions=false
  headingSize=awardedContractSummary.defaultHeadingSize
  headingClass=awardedContractSummary.defaultHeadingClass
>
    <@summaryViewWrapper.summaryViewItemWrapper
      idPrefix=awardedContractSummary.idPrefix
      headingPrefix=awardedContractSummary.headingPrefix
      displayOrder=awardedContractDiff.InfrastructureAwardedContractView_displayOrder.currentValue
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
          contractorName=awardedContractDiff.InfrastructureAwardedContractView_contractorName
          contractFunction=awardedContractDiff.InfrastructureAwardedContractView_contractFunction
          descriptionOfWork=awardedContractDiff.InfrastructureAwardedContractView_descriptionOfWork
          dateAwarded=awardedContractDiff.InfrastructureAwardedContractView_dateAwarded
          contractBand=awardedContractDiff.InfrastructureAwardedContractView_contractBand
          contactName=awardedContractDiff.InfrastructureAwardedContractView_contactName
          contactPhoneNumber=awardedContractDiff.InfrastructureAwardedContractView_contactPhoneNumber
          contactJobTitle=awardedContractDiff.InfrastructureAwardedContractView_contactJobTitle
          contactEmailAddress=awardedContractDiff.InfrastructureAwardedContractView_contactEmailAddress
          addedByPortalOrganisationGroup=awardedContractDiff.InfrastructureAwardedContractView_addedByPortalOrganisationGroup
        />
    </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>
