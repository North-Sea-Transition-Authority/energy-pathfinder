<#include '../../layout.ftl'>

<#assign defaultHeadingPrefix = "Upcoming tender"/>
<#assign idPrefix = "upcoming-tender"/>
<#assign defaultHeadingSize = "h2"/>
<#assign defaultHeadingClass = "govuk-heading-l"/>

<#macro workPlanUpcomingTenderSummary
  view
  headingPrefix=defaultHeadingPrefix
  showHeader=false
  showActions=false
  headingSize=defaultHeadingSize
  headingClass=defaultHeadingClass
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix=idPrefix
    headingPrefix=headingPrefix
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
      estimatedTenderStartDate=view.estimatedTenderStartDate
      contractBand=view.contractBand
      contractLength=view.contractLength
      contactName=view.contactName
      contactPhoneNumber=view.contactPhoneNumber
      contactJobTitle=view.contactJobTitle
      contactEmailAddress=view.contactEmailAddress
      addedByPortalOrganisationGroup=view.addedByPortalOrganisationGroup
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>

<#macro _upcomingTenderSummaryFields
  useDiffedField
  tenderDepartment=""
  descriptionOfWork=""
  estimatedTenderStartDate=""
  contractBand=""
  contractLength=""
  contactName=""
  contactPhoneNumber=""
  contactJobTitle=""
  contactEmailAddress=""
  addedByPortalOrganisationGroup=""
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
    fieldValue=estimatedTenderStartDate
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Contract band"
    fieldValue=contractBand
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Contract length"
    fieldValue=contractLength
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

<#macro upcomingTenderDiffSummary
  upcomingTenderDiff
  showHeader=false
  showActions=false
  headingSize=defaultHeadingSize
  headingClass=defaultHeadingClass
  headingPrefix=defaultHeadingPrefix
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix=idPrefix
    headingPrefix=headingPrefix
    displayOrder=upcomingTenderDiff.ForwardWorkPlanUpcomingTenderView_displayOrder.currentValue
    isValid=true
    summaryLinkList=[]
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
  >
    <@_upcomingTenderSummaryFields
      useDiffedField=true
      tenderDepartment=upcomingTenderDiff.ForwardWorkPlanUpcomingTenderView_tenderDepartment
      descriptionOfWork=upcomingTenderDiff.ForwardWorkPlanUpcomingTenderView_descriptionOfWork
      estimatedTenderStartDate=upcomingTenderDiff.ForwardWorkPlanUpcomingTenderView_estimatedTenderStartDate
      contractBand=upcomingTenderDiff.ForwardWorkPlanUpcomingTenderView_contractBand
      contractLength=upcomingTenderDiff.ForwardWorkPlanUpcomingTenderView_contractLength
      contactName=upcomingTenderDiff.ForwardWorkPlanUpcomingTenderView_contactName
      contactPhoneNumber=upcomingTenderDiff.ForwardWorkPlanUpcomingTenderView_contactPhoneNumber
      contactJobTitle=upcomingTenderDiff.ForwardWorkPlanUpcomingTenderView_contactJobTitle
      contactEmailAddress=upcomingTenderDiff.ForwardWorkPlanUpcomingTenderView_contactEmailAddress
      addedByPortalOrganisationGroup=upcomingTenderDiff.ForwardWorkPlanUpcomingTenderView_addedByPortalOrganisationGroup
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>