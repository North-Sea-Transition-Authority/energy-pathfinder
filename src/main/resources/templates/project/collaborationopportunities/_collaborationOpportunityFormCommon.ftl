<#include '../../layout.ftl'>

<#macro _collaborationOpportunityFormCommon
  collaborationOpportunityFormPath
  collaborationFunctionRestUrl
  preselectedCollaborationFunction
>
  <@fdsSearchSelector.searchSelectorRest
    path="${collaborationOpportunityFormPath}.function"
    selectorMinInputLength=0
    labelText="What function is the collaboration opportunity for?"
    restUrl=springUrl(collaborationFunctionRestUrl)
    preselectedItems=preselectedCollaborationFunction!{}
  />
  <@fdsTextarea.textarea
    path="${collaborationOpportunityFormPath}.descriptionOfWork"
    labelText="Provide a detailed description of the work"
    hintText="If the work is urgent, select Yes to the urgent response question below and note the deadline for responses in the description of the work"
  />
  <@fdsRadio.radioGroup
    path="${collaborationOpportunityFormPath}.urgentResponseNeeded"
    labelText="Does this collaboration opportunity need to be resolved in under 1 month?"
  >
    <@fdsRadio.radioYes path="${collaborationOpportunityFormPath}.urgentResponseNeeded"/>
    <@fdsRadio.radioNo path="${collaborationOpportunityFormPath}.urgentResponseNeeded"/>
  </@fdsRadio.radioGroup>
  <@contactDetails.standardContactDetails
    path="${collaborationOpportunityFormPath}.contactDetail"
    legendHeading="Opportunity contact details"
  />
</#macro>

<#macro _collaborationOpportunityDocumentUploadCommon collaborationOpportunityFormPath>
  <@fileUpload.fileUploadWithFieldSet
    id="collaboration-opportunity-file-upload-id"
    path="${collaborationOpportunityFormPath}.uploadedFileWithDescriptionForms"
    legendHeading="Supporting document"
    fieldsetHint="Optionally add a document to provide additional information for the supply chain"
    legendHeadingSize="h2"
    multipleFileUpload=false
  />
</#macro>