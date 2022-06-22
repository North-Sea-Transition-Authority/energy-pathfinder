<#include '../../../layout.ftl'/>
<#import '../_collaborationOpportunitySummaryCommon.ftl' as collaborationOpportunitySummaryCommon>

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
    <@collaborationOpportunitySummaryCommon._collaborationOpportunitySummaryFields
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
      addedByPortalOrganisationGroup=view.addedByPortalOrganisationGroup
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
    <@collaborationOpportunitySummaryCommon._collaborationOpportunitySummaryFields
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
      addedByPortalOrganisationGroup=diffModel.InfrastructureCollaborationOpportunityView_addedByPortalOrganisationGroup
    />
  </@summaryViewWrapper.summaryViewItemWrapper>
</#macro>
