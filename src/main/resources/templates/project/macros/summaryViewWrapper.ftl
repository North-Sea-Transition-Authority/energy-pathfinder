<#include '../../layout.ftl'>

<#macro summaryViewItemWrapper summaryView idPrefix headingPrefix showHeader=true showActions=true headingSize="h2" headingClass="govuk-heading-l">
  <#if showHeader>
    <#assign heading = headingPrefix + " " + summaryView.displayOrder />
  <#else>
    <#assign heading = "" />
  </#if>
  <#if summaryView.valid?has_content && !summaryView.valid>
    <#assign errorMessage = "${heading} is incomplete" />
  <#else>
    <#assign errorMessage = "" />
  </#if>
  <@fdsCheckAnswers.checkAnswersWrapper
    summaryListId="${idPrefix}-${summaryView.displayOrder}"
    summaryListErrorMessage="${errorMessage}"
    headingText="${heading}"
    headingSize=headingSize
    headingClass=headingClass
  >
    <#if showActions>
      <#list summaryView.summaryLinks as summaryLink>
        <@fdsAction.link
          linkText=summaryLink.linkText
          linkUrl=springUrl(summaryLink.url)
          linkScreenReaderText=heading
          linkClass="govuk-link govuk-!-font-size-19"
        />
      </#list>
    </#if>
    <@fdsCheckAnswers.checkAnswers>
      <#nested/>
    </@fdsCheckAnswers.checkAnswers>
  </@fdsCheckAnswers.checkAnswersWrapper>
</#macro>
