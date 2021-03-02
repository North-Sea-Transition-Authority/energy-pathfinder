<#include '../../layout.ftl'>

<@defaultPage htmlTitle="Upcoming tender" pageHeading="Upcoming tender" breadcrumbs=true errorItems=errorList>

  <@fdsForm.htmlForm>
    <@fdsSearchSelector.searchSelectorRest path="form.tenderFunction" selectorMinInputLength=0 labelText="What function is the tender for?" restUrl=springUrl(tenderRestUrl)  preselectedItems=preSelectedFunction!{} />
    <@fdsTextarea.textarea path="form.descriptionOfWork" labelText="Provide a detailed description of the work"/>
    <@fdsDateInput.dateInput
      dayPath="form.estimatedTenderDate.day"
      monthPath="form.estimatedTenderDate.month"
      yearPath="form.estimatedTenderDate.year"
      labelText="Estimated tender date"
      formId="estimatedTenderDate-day-month-year"
    />
    <@fdsRadio.radio
      labelText="Contract band"
      path="form.contractBand"
      radioItems=contractBands
    />
    <@contactDetails.standardContactDetails path="form.contactDetail" legendHeading="Tender contact details"/>
    <@fileUpload.fileUploadWithFieldSet
      id="upcoming-tender-file-upload-id"
      path="form.uploadedFileWithDescriptionForms"
      legendHeading="Tender document"
      fieldsetHint="Optionally add a document to provide additional information for the supply chain"
      legendHeadingSize="h2"
      multipleFileUpload=false
    />
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>