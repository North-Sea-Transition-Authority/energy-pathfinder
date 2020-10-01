<#include '../../layout.ftl'>

<@defaultPage htmlTitle="Collaboration opportunity" pageHeading="Collaboration opportunity" breadcrumbs=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>

  <@fdsForm.htmlForm>
    <@fdsSearchSelector.searchSelectorRest path="form.function" selectorMinInputLength=0 labelText="What function is the collaboration opportunity for?" restUrl=springUrl(collaborationFunctionRestUrl)  preselectedItems=preselectedCollaboration!{} />
    <@fdsTextarea.textarea path="form.descriptionOfWork" labelText="Provide a detailed description of the work"/>
    <@fdsDateInput.dateInput
      dayPath="form.estimatedServiceDate.day"
      monthPath="form.estimatedServiceDate.month"
      yearPath="form.estimatedServiceDate.year"
      labelText="Estimated service date"
      formId="estimatedServiceDate-day-month-year"
    />
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