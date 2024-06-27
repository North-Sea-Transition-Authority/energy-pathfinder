<#include '../../layout.ftl'/>

<#macro userAction userAction ariaDescribeById="">
  <#if userAction.enabled>
    <#if userAction.type = "LINK_BUTTON">
      <@_linkButton userAction=userAction/>
    <#elseif userAction.type = "LINK">
      <@_link userAction=userAction/>
    <#elseif userAction.type = "DASHBOARD_LINK">
      <@_dashboardLink userAction=userAction ariaDescribeById=ariaDescribeById applyNotVisitedClass=userAction.applyNotVisitedClass/>
    </#if>
  </#if>
</#macro>

<#macro _dashboardLink userAction ariaDescribeById applyNotVisitedClass=true>
  <#assign classes = "govuk-link govuk-!-font-size-24"/>
  <#if applyNotVisitedClass>
    <#assign classes = classes + " govuk-link--no-visited-state"/>
  </#if>
  <@_link
    userAction=userAction
    linkClass=classes
    ariaDescribeById=ariaDescribeById
  />
</#macro>

<#macro _linkButton userAction>
  <#assign linkClasses = "govuk-button"/>

  <#if userAction.buttonType.modifierValue?has_content>
    <#assign linkClasses = "govuk-button govuk-button--${userAction.buttonType.modifierValue}"/>
  </#if>
  <@fdsAction.link
    linkText=userAction.prompt
    linkUrl=springUrl(userAction.url)
    linkClass=linkClasses
    role=true
  />
</#macro>

<#macro _link userAction linkClass="govuk-link" ariaDescribeById="">
  <@fdsAction.link
    linkText=userAction.prompt
    linkUrl=springUrl(userAction.url)
    linkClass=linkClass
    role=false
    linkScreenReaderText=userAction.screenReaderText!""
    ariaDescribedBy=ariaDescribeById
  />
</#macro>
