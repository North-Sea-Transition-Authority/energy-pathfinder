<#include '../../layout.ftl'/>

<#macro userAction userAction ariaDescribedById="">
  <#if userAction.enabled>
    <#if userAction.type = "LINK_BUTTON">
      <@_linkButton userAction=userAction/>
    <#elseif userAction.type = "LINK">
      <@_link userAction=userAction/>
    <#elseif userAction.type = "DASHBOARD_LINK">
      <@_dashboardLink userAction=userAction ariaDescribedById=ariaDescribedById applyNotVisitedClass=userAction.applyNotVisitedClass/>
    </#if>
  </#if>
</#macro>

<#macro _dashboardLink userAction ariaDescribedById applyNotVisitedClass=true>
  <#assign classes = "govuk-link govuk-!-font-size-24"/>
  <#if applyNotVisitedClass>
    <#assign classes = classes + " govuk-link--no-visited-state"/>
  </#if>
  <@_link
    userAction=userAction
    linkClass=classes
    ariaDescribedById=ariaDescribedById
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

<#macro _link userAction linkClass="govuk-link" ariaDescribedById="">
  <@fdsAction.link
    linkText=userAction.prompt
    linkUrl=springUrl(userAction.url)
    linkClass=linkClass
    role=false
    linkScreenReaderText=userAction.screenReaderText!""
    ariaDescribedBy=ariaDescribedById
  />
</#macro>
