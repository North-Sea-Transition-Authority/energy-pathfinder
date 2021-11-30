<#include '../../layout.ftl'>
<#import './_platformFpsoSummary.ftl' as platformFpsoSummary>
<#import './_terminology.ftl' as terminology>

<#assign platformLowerCase = terminology.terminology['platformLowerCase'] />
<#assign floatingUnitLowerCase = terminology.terminology['floatingUnitLowerCase'] />

<#assign title = "Are you sure you want to remove ${platformLowerCase} or ${floatingUnitLowerCase} ${displayOrder}?"/>

<@defaultPage htmlTitle=title pageHeading=title breadcrumbs=true>

  <@platformFpsoSummary.platformFpsoSummary
    view=view
    showHeader=false
    showActions=false
  />

  <@fdsForm.htmlForm>
    <@fdsAction.submitButtons
      primaryButtonText="Remove"
      primaryButtonClass="govuk-button govuk-button--warning"
      secondaryLinkText="Cancel"
      linkSecondaryAction=true
      linkSecondaryActionUrl=springUrl(cancelUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>