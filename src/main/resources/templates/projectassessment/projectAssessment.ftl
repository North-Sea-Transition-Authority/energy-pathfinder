<#include '../layout.ftl'>

<@defaultPage htmlTitle=pageName pageHeading="" breadcrumbs=true fullWidthColumn=true>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>

  <@noEscapeHtml.noEscapeHtml html=projectHeaderHtml />

  <h2 class="govuk-heading-l">${pageName}</h2>

  <@fdsForm.htmlForm>
    <#if !canRequestUpdate>
      <@fdsInsetText.insetText insetTextClass="govuk-inset-text--yellow">
        An update is already in progress for this project. As a result you will not be able to request an update as part of this assessment
      </@fdsInsetText.insetText>
    </#if>
    <@fdsRadio.radioGroup path="form.readyToBePublished" labelText="Is the project ready to be published?" hiddenContent=true>
      <@fdsRadio.radioYes path="form.readyToBePublished">
        <#if canRequestUpdate>
          <@fdsRadio.radioGroup path="form.updateRequired" nestingPath="form.readyToBePublished" labelText="Does this project require an update?">
            <@fdsRadio.radioYes path="form.updateRequired" />
            <@fdsRadio.radioNo path="form.updateRequired" />
          </@fdsRadio.radioGroup>
        </#if>
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
