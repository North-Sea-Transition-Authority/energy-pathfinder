<#include '../../layout.ftl'>

<#assign idPrefix = "awarded-contract" />
<#assign headingPrefix = "Awarded contract" />
<#assign defaultHeadingSize = "h2" />
<#assign defaultHeadingClass = "govuk-heading-l" />

<#macro awardedContractSummary
  awardedContractView
  showHeader=false
  showActions=false
  headingSize=defaultHeadingSize
  headingClass=defaultHeadingClass
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix=idPrefix
    headingPrefix=headingPrefix
    displayOrder=awardedContractView.displayOrder
    isValid=awardedContractView.valid!""
    summaryLinkList=awardedContractView.summaryLinks
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
  >
    <@_awardedContractSummaryFields
      useDiffedField=false
      contractorName=awardedContractView.contractorName
      contractFunction=awardedContractView.contractFunction
      descriptionOfWork=awardedContractView.descriptionOfWork
      dateAwarded=awardedContractView.dateAwarded
      contractBand=awardedContractView.contractBand
      contactName=awardedContractView.contactName
      contactPhoneNumber=awardedContractView.contactPhoneNumber
      contactJobTitle=awardedContractView.contactJobTitle
      contactEmailAddress=awardedContractView.contactEmailAddress
      addedByPortalOrganisationGroup=awardedContractView.addedByPortalOrganisationGroup
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>

<#macro forwardWorkPlanAwardedContractDiffSummary
  awardedContractDiff
  showHeader=false
  showActions=false
  headingSize=defaultHeadingSize
  headingClass=defaultHeadingClass
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix=idPrefix
    headingPrefix=headingPrefix
    displayOrder=awardedContractDiff.ForwardWorkPlanAwardedContractView_displayOrder.currentValue
    isValid=true
    summaryLinkList=[]
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
    diffObject=awardedContractDiff
  >
    <@_awardedContractSummaryFields
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

<#macro infrastructureAwardedContractDiffSummary
awardedContractDiff
showHeader=false
showActions=false
headingSize=defaultHeadingSize
headingClass=defaultHeadingClass
>
    <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix=idPrefix
    headingPrefix=headingPrefix
    displayOrder=awardedContractDiff.InfrastructureAwardedContractView_displayOrder.currentValue
    isValid=true
    summaryLinkList=[]
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
    diffObject=awardedContractDiff
    >
        <@_awardedContractSummaryFields
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

<#macro _awardedContractSummaryFields
  useDiffedField
  contractorName=""
  contractFunction=""
  descriptionOfWork=""
  dateAwarded=""
  contractBand=""
  contactName=""
  contactPhoneNumber=""
  contactJobTitle=""
  contactEmailAddress=""
  addedByPortalOrganisationGroup=""
>
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Contractor name"
    fieldValue=contractorName
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardNestedOrDiffRow
    prompt="Contract function"
    fieldValue=contractFunction
    isDiffedField=useDiffedField
  >
    <@stringWithTag.stringWithTag stringWithTag=contractFunction />
  </@checkAnswers.checkAnswersStandardNestedOrDiffRow>
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Description of work"
    fieldValue=descriptionOfWork
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Date awarded"
    fieldValue=dateAwarded
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Contract band"
    fieldValue=contractBand
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Contact name"
    fieldValue=contactName
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Phone number"
    fieldValue=contactPhoneNumber
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Job title"
    fieldValue=contactJobTitle
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersRowEmailOrDiff
    prompt="Email address"
    fieldValue=contactEmailAddress
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Added by"
    fieldValue=addedByPortalOrganisationGroup
    isDiffedField=useDiffedField
  />
</#macro>
