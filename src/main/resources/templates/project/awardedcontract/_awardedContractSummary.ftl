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

<#macro awardedContractDiffSummary
  awardedContractDiff
  showHeader=false
  showActions=false
  headingSize=defaultHeadingSize
  headingClass=defaultHeadingClass
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix=idPrefix
    headingPrefix=headingPrefix
    displayOrder=awardedContractDiff.AwardedContractView_displayOrder.currentValue
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
      contractorName=awardedContractDiff.AwardedContractView_contractorName
      contractFunction=awardedContractDiff.AwardedContractView_contractFunction
      descriptionOfWork=awardedContractDiff.AwardedContractView_descriptionOfWork
      dateAwarded=awardedContractDiff.AwardedContractView_dateAwarded
      contractBand=awardedContractDiff.AwardedContractView_contractBand
      contactName=awardedContractDiff.AwardedContractView_contactName
      contactPhoneNumber=awardedContractDiff.AwardedContractView_contactPhoneNumber
      contactJobTitle=awardedContractDiff.AwardedContractView_contactJobTitle
      contactEmailAddress=awardedContractDiff.AwardedContractView_contactEmailAddress
      addedByPortalOrganisationGroup=awardedContractDiff.AwardedContractView_addedByPortalOrganisationGroup
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
