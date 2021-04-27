<#include '../../layout.ftl'>

<#assign defaultHeadingPrefix = "Upcoming tender"/>
<#assign idPrefix = "upcoming-tender"/>
<#assign defaultHeadingSize = "h2"/>
<#assign defaultHeadingClass = "govuk-heading-l"/>

<#macro workPlanUpcomingTenderSummary
  view
  tenderName=defaultHeadingPrefix
  showHeader=false
  showActions=false
  headingSize=defaultHeadingSize
  headingClass=defaultHeadingClass
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix=idPrefix
    headingPrefix=tenderName
    displayOrder=view.displayOrder
    isValid=view.valid!""
    summaryLinkList=view.summaryLinks
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
  >
    <@_upcomingTenderSummaryFields
      useDiffedField=false
      tenderDepartment=view.tenderDepartment
      descriptionOfWork=view.descriptionOfWork
      estimatedTenderDate=view.estimatedTenderDate
      contractBand=view.contractBand
      contactName=view.contactName
      contactPhoneNumber=view.contactPhoneNumber
      contactJobTitle=view.contactJobTitle
      contactEmailAddress=view.contactEmailAddress
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>

<#macro _upcomingTenderSummaryFields
  useDiffedField
  tenderDepartment=""
  descriptionOfWork=""
  estimatedTenderDate=""
  contractBand=""
  contactName=""
  contactPhoneNumber=""
  contactJobTitle=""
  contactEmailAddress=""
>
  <@checkAnswers.checkAnswersStandardNestedOrDiffRow
    prompt="Department"
    fieldValue=tenderDepartment
    isDiffedField=useDiffedField
  >
    <@stringWithTag.stringWithTag stringWithTag=tenderDepartment />
  </@checkAnswers.checkAnswersStandardNestedOrDiffRow>
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Description of work"
    fieldValue=descriptionOfWork
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Estimated tender date"
    fieldValue=estimatedTenderDate
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Contract band"
    fieldValue=contractBand
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Name"
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
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Email address"
    fieldValue=contactEmailAddress
    isDiffedField=useDiffedField
  />
</#macro>