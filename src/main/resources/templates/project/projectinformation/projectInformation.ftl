<#include '../../layout.ftl'>

<@defaultPage htmlTitle="Project information" pageHeading="Project information" breadcrumbs=true>
  <#if errorList?has_content>
      <@fdsError.errorSummary errorItems=errorList />
  </#if>

  <@fdsForm.htmlForm>
    <@fdsRadio.radioGroup
      labelText="What is the field stage?"
      path="form.fieldStage"
      fieldsetHeadingClass="govuk-fieldset__legend"
    >
      <#list fieldStages as fieldStage>
        <#assign fieldStageName = fieldStage.name()/>
        <#assign displayName = fieldStage.getNameAndDescription()/>
        <#assign displayOrder = fieldStage.displayOrder/>
        <@fdsRadio.radioItem path="form.fieldStage" itemMap={fieldStageName: displayName} isFirstItem=displayOrder=1 />
      </#list>
    </@fdsRadio.radioGroup>

      <@fdsTextInput.textInput path="form.projectTitle" labelText="What is the project title?" labelClass="govuk-label " inputClass="govuk-!-width-two-thirds"/>
      <@fdsTextarea.textarea path="form.projectSummary" labelText="Provide a summary of the project" labelClass="govuk-label " />
      <@fdsDetails.summaryDetails summaryTitle="What should I provide for the summary of the project?">
        <p class="govuk-body">Provide a description of the project and future plans and include the following where appropriate:</p>
          <ul class="govuk-list govuk-list--bullet">
            <li>Location in UKCS and type of structure e.g. steel platform, FPSO, Subsea manifold etc.</li>
            <li>Current stage of project e.g. Pre-Feed, COP etc.</li>
            <li>Timing/Schedule for work e.g. dates for FID, FDP approval, COP, Decommissioning Programme approval etc.</li>
          </ul>
      </@fdsDetails.summaryDetails>
      <@fdsFieldset.fieldset legendHeading="Contact details" legendHeadingSize="h2" >
          <@fdsTextInput.textInput path="form.name" labelText="Name" labelClass="govuk-label " inputClass="govuk-!-width-two-thirds"/>
          <@fdsTextInput.textInput path="form.phoneNumber" labelText="Telephone number" hintText="Enter a UK telephone or mobile number" labelClass="govuk-label " inputClass="govuk-!-width-two-thirds"/>
          <@fdsTextInput.textInput path="form.jobTitle" labelText="Job title" labelClass="govuk-label " inputClass="govuk-!-width-two-thirds"/>
          <@fdsTextInput.textInput path="form.emailAddress" labelText="Email address" labelClass="govuk-label " inputClass="govuk-!-width-two-thirds"/>
      </@fdsFieldset.fieldset>
      <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>