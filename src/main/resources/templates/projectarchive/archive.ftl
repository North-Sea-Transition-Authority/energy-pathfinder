<#include '../layout.ftl'>

<#assign title = pageHeading>

<@defaultPage htmlTitle=title breadcrumbs=true fullWidthColumn=true errorItems=errorList>

  <@noEscapeHtml.noEscapeHtml html=projectHeaderHtml />

  <h2 class="govuk-heading-l">${title}</h2>

  <@fdsInsetText.insetText insetTextClass="govuk-inset-text--yellow">
    By archiving this ${projectTypeDisplayNameLowercase}, any in progress updates will no longer be able to be submitted.
  </@fdsInsetText.insetText>

  <@fdsForm.htmlForm>
    <@fdsTextarea.textarea
      path="form.archiveReason"
      labelText="What is the reason you are archiving this ${projectTypeDisplayNameLowercase}?"
    />
    <@fdsAction.submitButtons
      primaryButtonText="Save and complete"
      linkSecondaryAction=true
      secondaryLinkText="Cancel"
      linkSecondaryActionUrl=springUrl(cancelUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>
