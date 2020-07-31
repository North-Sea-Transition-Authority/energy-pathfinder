<#include '../../layout.ftl'>

<@defaultPage htmlTitle="Project information" pageHeading="Project information" breadcrumbs=true>
  <#assign summaryGuidance = "Provide a description of the project and future plans and include the following where appropriate:
    Location in UKCS and type of structure e.g. steel platform, FPSO, Subsea manifold etc.
    Current stage of project e.g. Pre-Feed, COP etc.
    Timing/Schedule for work e.g. dates for FID, FDP approval, COP, Decommissioning Programme approval etc."
  >
  <#if errorList?has_content>
      <@fdsError.errorSummary errorItems=errorList errorTitle="Errors"/>
  </#if>

  <@fdsForm.htmlForm>
    <@fdsRadio.radioGroup
      labelText="What is the field stage?"
      path="form.fieldStage"
      fieldsetHeadingClass="govuk-fieldset__legend--l"
      fieldsetHeadingSize="h2"
    >
      <#list fieldStages as fieldStage>
        <#assign fieldStageName = fieldStage.name()/>
        <#assign displayName = fieldStage.getDisplayName()/>
        <#assign description = fieldStage.getDescription()/>
        <#assign displayOrder = fieldStage.getDisplayOrder()/>
        <@fdsRadio.radioItem path="form.fieldStage" itemMap={fieldStageName: displayName + " - " + description} isFirstItem=displayOrder=1 />
      </#list>
    </@fdsRadio.radioGroup>

      <@fdsTextInput.textInput path="form.projectTitle" labelText="What is the project title?" labelClass="govuk-label--m" inputClass="govuk-!-width-two-thirds"/>
      <@fdsTextarea.textarea path="form.projectSummary" labelText="Provide a summary of the project" labelClass="govuk-label--m" hintText=summaryGuidance />

      <@fdsAction.submitButtons primaryButtonText="Complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>