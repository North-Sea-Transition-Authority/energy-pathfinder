<#include '../../layout.ftl'>

<@defaultPage htmlTitle="Collaboration opportunity" pageHeading="Collaboration opportunity" breadcrumbs=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>

  <@fdsForm.htmlForm>
    <@fdsSearchSelector.searchSelectorRest path="form.function" selectorMinInputLength=0 labelText="What function is the collaboration opportunity for?" restUrl=springUrl(collaborationFunctionRestUrl)  preselectedItems=preselectedCollaboration!{} />
    <@fdsTextarea.textarea
      path="form.descriptionOfWork"
      labelText="Provide a detailed description of the work"
      hintText="If the work is urgent, select Yes to the urgent response question below and note the deadline for responses in the description of the work"
    />
    <@fdsRadio.radioGroup path="form.urgentResponseNeeded" labelText="Is an urgent response required from the supply chain?">
      <@fdsRadio.radioYes path="form.urgentResponseNeeded"/>
      <@fdsRadio.radioNo path="form.urgentResponseNeeded"/>
    </@fdsRadio.radioGroup>
    <@contactDetails.standardContactDetails path="form.contactDetail" legendHeading="Opportunity contact details"/>
    <@fileUpload.fileUploadWithFieldSet
      id="collaboration-opportunity-file-upload-id"
      path="form.uploadedFileWithDescriptionForms"
      legendHeading="Opportunity document"
      fieldsetHint="Optionally add a document to provide additional information for the supply chain"
      legendHeadingSize="h2"
      multipleFileUpload=false
    />
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>