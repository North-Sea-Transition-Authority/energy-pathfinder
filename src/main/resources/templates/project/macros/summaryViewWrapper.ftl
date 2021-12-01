<#include '../../layout.ftl'>

<#macro summaryViewItemWrapper
  idPrefix
  headingPrefix
  displayOrder
  isValid
  summaryLinkList=[]
  showHeader=true
  showActions=true
  headingSize="h2"
  headingClass="govuk-heading-l"
>
  <#if showHeader>
    <#assign heading = headingPrefix + " " + displayOrder />
  <#else>
    <#assign heading = "" />
  </#if>
  <#if isValid?has_content && !isValid>
    <#assign errorMessage = "${heading} is incomplete" />
  <#else>
    <#assign errorMessage = "" />
  </#if>
  <@fdsCheckAnswers.checkAnswersWrapper
    summaryListId="${idPrefix}-${displayOrder}"
    summaryListErrorMessage="${errorMessage}"
    headingText="${heading}"
    headingSize=headingSize
    headingClass=headingClass
  >
    <#if showActions>
      <#list summaryLinkList as summaryLink>
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
