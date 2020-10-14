<#include '../../layout.ftl'>
<#import './_platformFpsoSummary.ftl' as platformFpsoSummary>

<#assign title = "Are you sure you want to remove Platform or FPSO " + displayOrder/>

<@defaultPage htmlTitle=title pageHeading=title breadcrumbs=true>

    <@platformFpsoSummary.platformFpsoSummary view=view/>

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