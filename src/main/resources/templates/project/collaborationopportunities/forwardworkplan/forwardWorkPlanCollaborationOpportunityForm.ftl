<#include '../../../layout.ftl'>
<#import '../_collaborationOpportunityFormCommon.ftl' as collaborationOpportunity>

<@defaultPage htmlTitle=pageHeading pageHeading=pageHeading breadcrumbs=true errorItems=errorList>
  <@fdsForm.htmlForm>
    <@collaborationOpportunity._collaborationOpportunityFormCommon
      collaborationOpportunityFormPath="form"
      collaborationFunctionRestUrl=collaborationFunctionRestUrl
      preselectedCollaborationFunction=preselectedFunction!{}
    />
    <@collaborationOpportunity._collaborationOpportunityDocumentUploadCommon
      collaborationOpportunityFormPath="form"
    />
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>