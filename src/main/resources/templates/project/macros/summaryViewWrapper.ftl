<#include '../../layout.ftl'>

<#macro summaryViewItemWrapper summaryView idPrefix headingPrefix showHeader=true showActions=true headingSize="h2" headingClass="govuk-heading-l">
  <#assign heading = headingPrefix + " " + summaryView.displayOrder />
  <div class="summary-list__item">
    <#if showHeader>
      <${headingSize} id="${idPrefix}-${summaryView.displayOrder}" class="${headingClass} summary-list__heading">
        ${heading}
      </${headingSize}>
    </#if>
    <@fdsCheckAnswers.checkAnswers>
      <#if showActions>
        <div class="summary-list__actions">
          <#list summaryView.summaryLinks as summaryLink>
            <@fdsAction.link
              linkText=summaryLink.linkText
              linkUrl=springUrl(summaryLink.url)
              linkScreenReaderText=heading
            />
          </#list>
        </div>
      </#if>
      <#if summaryView.valid?has_content && !summaryView.valid>
        <span class="govuk-error-message">
          <span class="govuk-visually-hidden">Error:</span>${heading} is incomplete
        </span>
      </#if>
      <#nested/>
    </@fdsCheckAnswers.checkAnswers>
  </div>
</#macro>