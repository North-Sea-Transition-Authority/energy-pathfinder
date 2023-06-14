<#-- @ftlvariable name="differenceFreemarkerService" type="uk.co.ogauthority.pathfinder.service.difference.DifferenceFreemarkerService" -->
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
  diffObject=""
>
  <#assign isDiffObjectDeleted = diffObject?has_content && differenceFreemarkerService.areAllFieldsDeleted(diffObject) />

    <#assign viewInner>
      <@_summaryViewItemWrapperInner
        idPrefix=idPrefix
        headingPrefix=headingPrefix
        displayOrder=displayOrder
        isValid=isValid
        summaryLinkList=summaryLinkList
        showHeader=showHeader
        showActions=showActions
        headingSize=headingSize
        headingClass=headingClass
      >
          <#nested/>
      </@_summaryViewItemWrapperInner>
  </#assign>
  <#if !isDiffObjectDeleted>
    ${viewInner}
  <#else>
    <div class="diff-changes">
      <div>
          ${viewInner}
      </div>
    </div>
  </#if>
</#macro>

<#macro _summaryViewItemWrapperInner
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