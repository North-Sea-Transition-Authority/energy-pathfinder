<#include '../../../layout.ftl'>
<#import '../_collaborationOpportunityFormCommon.ftl' as collaborationOpportunity>

<@defaultPage htmlTitle="Collaboration opportunity" pageHeading="Collaboration opportunity" breadcrumbs=true errorItems=errorList>
  <@fdsForm.htmlForm>
    <@collaborationOpportunity._collaborationOpportunityFormCommon
      collaborationOpportunityFormPath="form"
      collaborationFunctionRestUrl=collaborationFunctionRestUrl
      preselectedCollaborationFunction=preselectedCollaboration!{}
    />
    <@collaborationOpportunity._collaborationOpportunityDocumentUploadCommon
      collaborationOpportunityFormPath="form"
    />
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>