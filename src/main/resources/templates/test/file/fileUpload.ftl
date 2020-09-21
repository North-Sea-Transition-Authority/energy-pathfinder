<#include '../../layout.ftl'>

<@defaultPage htmlTitle="File upload test" pageHeading="File upload test">
  <@fdsForm.htmlForm>
    <@fdsFieldset.fieldset
      legendHeading="Some documents"
      legendHeadingClass="govuk-fieldset__legend--s"
      legendHeadingSize="h2"
      hintText="Upload some supporting documents"
    >
      <@fdsFileUpload.fileUpload
        id="test-upload-file-id"
        path="form.uploadedFileWithDescriptionForms"
        uploadUrl=uploadUrl
        deleteUrl=deleteUrl
        maxAllowedSize=fileuploadMaxUploadSize
        allowedExtensions=fileuploadAllowedExtensions
        downloadUrl=downloadUrl
        existingFiles=uploadedFileViewList
        dropzoneText="Drag and drop your documents here"
      />
    </@fdsFieldset.fieldset>
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>