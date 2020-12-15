<#include '../layout.ftl'>

<@defaultPage htmlTitle=pageName pageHeading="" breadcrumbs=true twoThirdsColumn=false>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>

  <@noEscapeHtml.noEscapeHtml html=projectHeaderHtml />

  <h2 class="govuk-heading-l">${pageName}</h2>

  <@fdsForm.htmlForm>
    <@fdsRadio.radio path="form.projectQuality" labelText="What is the quality of the project?" radioItems=projectQualities />
    <@fdsRadio.radioGroup path="form.readyToBePublished" labelText="Is the project ready to be published?" hiddenContent=true>
      <@fdsRadio.radioYes path="form.readyToBePublished">
        <@fdsRadio.radioGroup path="form.updateRequired" nestingPath="form.readyToBePublished" labelText="Does this project require an update?">
          <@fdsRadio.radioYes path="form.updateRequired" />
          <@fdsRadio.radioNo path="form.updateRequired" />
        </@fdsRadio.radioGroup>
      </@fdsRadio.radioYes>
      <@fdsRadio.radioNo path="form.readyToBePublished"/>
    </@fdsRadio.radioGroup>
    <@fdsAction.submitButtons
      primaryButtonText="Save and complete"
      linkSecondaryAction=true
      secondaryLinkText="Cancel"
      linkSecondaryActionUrl=springUrl(cancelUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>
