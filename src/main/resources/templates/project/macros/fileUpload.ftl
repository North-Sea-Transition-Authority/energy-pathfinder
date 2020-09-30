<#include '../../layout.ftl'/>
<!--
  Macros within this file are designed to be used with controllers
  that extend the PathfinderFileUploadController.class.

  The parameters passed to the fdsFileUpload.fileUpload macro
  for uploadUrl, deleteUrl, maxAllowedSize, allowedExtensions
  and existingFiles are all passed via PathfinderFileUploadController
-->

<#macro standardFileUpload id path multipleFileUpload=false>
  <@fdsFileUpload.fileUpload
    id=id
    path=path
    uploadUrl=uploadUrl
    deleteUrl=deleteUrl
    maxAllowedSize=fileuploadMaxUploadSize
    allowedExtensions=fileuploadAllowedExtensions
    downloadUrl=downloadUrl
    existingFiles=uploadedFileViewList
    multiFile=multipleFileUpload
  />
</#macro>

<#macro fileUploadWithFieldSet
  id
  path
  legendHeading
  fieldsetHint=""
  legendHeadingSize="h2"
  multipleFileUpload=false
>
  <@fdsFieldset.fieldset
    legendHeading=legendHeading
    hintText=fieldsetHint
    legendHeadingSize=legendHeadingSize
  >
    <@standardFileUpload
      id=id
      path=path
      multipleFileUpload=multipleFileUpload
    />
  </@fdsFieldset.fieldset>
</#macro>