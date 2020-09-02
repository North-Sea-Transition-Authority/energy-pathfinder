<#include '../../layout.ftl'>

<#macro contactDetails legendHeading="Contact details">
    <@fdsFieldset.fieldset legendHeading=legendHeading legendHeadingSize="h2" >
        <@fdsTextInput.textInput path="form.name" labelText="Name" labelClass="govuk-label " />
        <@fdsTextInput.textInput path="form.phoneNumber" labelText="Telephone number" hintText="Enter a UK telephone or mobile number" labelClass="govuk-label " />
        <@fdsTextInput.textInput path="form.jobTitle" labelText="Job title" labelClass="govuk-label " />
        <@fdsTextInput.textInput path="form.emailAddress" labelText="Email address" labelClass="govuk-label " />
    </@fdsFieldset.fieldset>
</#macro>