<#include '../../layout.ftl'/>

<#assign defaultHeadingPrefix = "Collaboration opportunity" />
<#assign idPrefix = "collaboration-opportunity" />
<#assign defaultHeadingSize = "h2" />
<#assign defaultHeadingClass = "govuk-heading-l" />

<#macro collaborationOpportunitySummary
  view
  opportunityName=defaultHeadingPrefix
  showHeader=false
  showActions=false
  headingSize=defaultHeadingSize
  headingClass=defaultHeadingClass
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix=idPrefix
    headingPrefix=opportunityName
    displayOrder=view.displayOrder
    isValid=view.valid!""
    summaryLinkList=view.summaryLinks
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
  >
    <@_collaborationOpportunitySummaryFields
      useDiffedField=false
      function=view.function
      descriptionOfWork=view.descriptionOfWork
      urgentResponseNeeded=view.urgentResponseNeeded
      contactName=view.contactName
      contactPhoneNumber=view.contactPhoneNumber
      contactJobTitle=view.contactJobTitle
      contactEmailAddress=view.contactEmailAddress
      uploadedFileUrl=(view.uploadedFileViews[0].fileUrl)!""
      uploadedFileName=(view.uploadedFileViews[0].fileName)!""
      uploadedFileDescription=(view.uploadedFileViews[0].fileDescription)!""
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>

<#macro collaborationOpportunityDiffSummary
  diffModel
  files=[]
  opportunityName=defaultHeadingPrefix
  showHeader=false
  showActions=false
  headingSize=defaultHeadingSize
  headingClass=defaultHeadingClass
>
  <@summaryViewWrapper.summaryViewItemWrapper
    idPrefix=idPrefix
    headingPrefix=opportunityName
    displayOrder=diffModel.InfrastructureCollaborationOpportunityView_displayOrder.currentValue
    isValid=true
    summaryLinkList=[]
    showHeader=showHeader
    showActions=showActions
    headingSize=headingSize
    headingClass=headingClass
  >
    <@_collaborationOpportunitySummaryFields
      useDiffedField=true
      function=diffModel.InfrastructureCollaborationOpportunityView_function
      descriptionOfWork=diffModel.InfrastructureCollaborationOpportunityView_descriptionOfWork
      urgentResponseNeeded=diffModel.InfrastructureCollaborationOpportunityView_urgentResponseNeeded
      contactName=diffModel.InfrastructureCollaborationOpportunityView_contactName
      contactPhoneNumber=diffModel.InfrastructureCollaborationOpportunityView_contactPhoneNumber
      contactJobTitle=diffModel.InfrastructureCollaborationOpportunityView_contactJobTitle
      contactEmailAddress=diffModel.InfrastructureCollaborationOpportunityView_contactEmailAddress
      uploadedFileUrl=(files[0].UploadedFileView_fileUrl)!""
      uploadedFileName=(files[0].UploadedFileView_fileName)!""
      uploadedFileDescription=(files[0].UploadedFileView_fileDescription)!""
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>

<#macro _collaborationOpportunitySummaryFields
  useDiffedField
  function=""
  descriptionOfWork=""
  urgentResponseNeeded=""
  contactName=""
  contactPhoneNumber=""
  contactJobTitle=""
  contactEmailAddress=""
  uploadedFileUrl=""
  uploadedFileName=""
  uploadedFileDescription=""
>
  <@checkAnswers.checkAnswersStandardNestedOrDiffRow
    prompt="Opportunity function"
    fieldValue=function
    isDiffedField=useDiffedField
  >
    <@stringWithTag.stringWithTag stringWithTag=function />
  </@checkAnswers.checkAnswersStandardNestedOrDiffRow>
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Description of work"
    fieldValue=descriptionOfWork
    isDiffedField=useDiffedField
  />
  <@checkAnswers.checkAnswersStandardOrDiffRow
    prompt="Urgent response required"
    fieldValue=urgentResponseNeeded
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
  <@checkAnswers.checkAnswersStandardOrDiffUploadedFileViewRow
    fileUrlFieldValue=uploadedFileUrl
    fileNameFieldValue=uploadedFileName
    fileDescriptionFieldValue=uploadedFileDescription
    isDiffedField=useDiffedField
  />
</#macro>
