<#include '../../layout.ftl'>

<@defaultPage htmlTitle="Project information" pageHeading="Project information" breadcrumbs=true>
  <#if errorList?has_content>
      <@fdsError.errorSummary errorItems=errorList />
  </#if>

  <@fdsForm.htmlForm>
    <@fdsRadio.radioGroup
      labelText="What is the field stage?"
      path="form.fieldStage"
      fieldsetHeadingClass="govuk-fieldset__legend--s"
    >
      <#list fieldStages as fieldStage>
        <#assign fieldStageName = fieldStage.name()/>
        <#assign displayName = fieldStage.getNameAndDescription()/>
        <#assign displayOrder = fieldStage.displayOrder/>
        <@fdsRadio.radioItem path="form.fieldStage" itemMap={fieldStageName: displayName} isFirstItem=displayOrder=1 />
      </#list>
    </@fdsRadio.radioGroup>

    <@fdsTextInput.textInput path="form.projectTitle" labelText="What is the project title?"/>
    <@fdsTextarea.textarea path="form.projectSummary" labelText="Provide a summary of the project"  />
    <@fdsDetails.summaryDetails summaryTitle="What should I provide for the summary of the project?">
      <p class="govuk-body">Provide a description of the project and future plans and include the following where appropriate:</p>
        <ul class="govuk-list govuk-list--bullet">
          <li>location in UKCS and type of structure, for example: steel platform, FPSO, Subsea manifold</li>
          <li>current stage of project, for example: Pre-Feed, COP</li>
          <li>timing / schedule for work, for example: dates for FID, FDP approval, COP, Decommissioning Programme approval</li>
        </ul>
    </@fdsDetails.summaryDetails>
    <@contactDetails.standardContactDetails path="form.contactDetail"/>
    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>