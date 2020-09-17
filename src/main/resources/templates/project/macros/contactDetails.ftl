<#include '../../layout.ftl'>

<#macro standardContactDetails path legendHeading="Contact details">
  <@customContactDetails
    namePath="${path + '.name'}"
    phoneNumberPath="${path + '.phoneNumber'}"
    jobTitlePath="${path + '.jobTitle'}"
    emailAddressPath="${path + '.emailAddress'}"
    legendHeading=legendHeading
  />
</#macro>

<#macro
  customContactDetails
  namePath
  phoneNumberPath
  jobTitlePath
  emailAddressPath
  legendHeading="Contact details"
  legendHeadingSize="h2"
  nameLabelText="Name"
  phoneNumberLabelText="Telephone number"
  jobTitleLabelText="Job title"
  emailAddressLabelText="Email address"
>
  <@fdsFieldset.fieldset legendHeading=legendHeading legendHeadingSize=legendHeadingSize>
    <@fdsTextInput.textInput path=namePath labelText=nameLabelText/>
    <@fdsTextInput.textInput path=phoneNumberPath labelText=phoneNumberLabelText hintText="Enter a telephone or mobile number, if entering an international number provide the country code"/>
    <@fdsTextInput.textInput path=jobTitlePath labelText=jobTitleLabelText/>
    <@fdsTextInput.textInput path=emailAddressPath labelText=emailAddressLabelText/>
  </@fdsFieldset.fieldset>
</#macro>