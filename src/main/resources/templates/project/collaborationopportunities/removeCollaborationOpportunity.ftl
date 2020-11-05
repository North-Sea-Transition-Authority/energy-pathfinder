<#include '../../layout.ftl'>
<#import './collaborationOpportunitySummary.ftl' as collaborationOpportunitySummary>

<#assign title = "Are you sure you want to remove collaboration opportunity " + displayOrder/>

<@defaultPage htmlTitle=title pageHeading=title breadcrumbs=true>

    <@collaborationOpportunitySummary.collaborationOpportunitySummary
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