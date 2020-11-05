<#include '../../layout.ftl'>

<#macro summaryViewItemWrapper summaryView idPrefix headingPrefix showHeader=true showActions=true headingSize="h2" headingClass="govuk-heading-l">
  <#assign heading = headingPrefix + " " + summaryView.displayOrder />
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
        />
      </#list>
    </#if>
    <@fdsCheckAnswers.checkAnswers>
      <#nested/>
    </@fdsCheckAnswers.checkAnswers>
  </@fdsCheckAnswers.checkAnswersWrapper>
</#macro>
