<#include '../../layout.ftl'>

<#macro standardContactDetails path legendHeading="Contact details" hintText="">
  <@customContactDetails
    namePath="${path + '.name'}"
    phoneNumberPath="${path + '.phoneNumber'}"
    jobTitlePath="${path + '.jobTitle'}"
    emailAddressPath="${path + '.emailAddress'}"
    hintText=hintText
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
  hintText=""
  nameLabelText="Name"
  phoneNumberLabelText="Telephone number"
  jobTitleLabelText="Job title"
  emailAddressLabelText="Email address"
>
  <@fdsFieldset.fieldset legendHeading=legendHeading legendHeadingSize=legendHeadingSize hintText=hintText>
    <@fdsTextInput.textInput path=namePath labelText=nameLabelText/>
    <@fdsTextInput.textInput path=phoneNumberPath labelText=phoneNumberLabelText hintText="Enter a telephone or mobile number. For international numbers provide the country code"/>
    <@fdsTextInput.textInput path=jobTitlePath labelText=jobTitleLabelText/>
    <@fdsTextInput.textInput path=emailAddressPath labelText=emailAddressLabelText/>
  </@fdsFieldset.fieldset>
</#macro>
