<#include '../../layout.ftl'/>

<#macro userAction userAction>
  <#if userAction.enabled>
    <#if userAction.type = "LINK_BUTTON">
      <@_linkButton userAction=userAction/>
    <#elseif userAction.type = "LINK">
      <@_link userAction=userAction/>
    </#if>
  </#if>
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

<#macro _link userAction>
  <@fdsAction.link
    linkText=userAction.prompt
    linkUrl=springUrl(userAction.url)
    linkClass="govuk-link"
    role=false
    linkScreenReaderText=userAction.screenReaderText!""
  />
</#macro>