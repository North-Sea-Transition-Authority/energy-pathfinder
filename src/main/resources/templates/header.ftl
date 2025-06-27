<#include 'layout.ftl'/>

<#-- @ftlvariable name="currentUserView" type="uk.co.ogauthority.pathfinder.auth.CurrentUserView" -->

<#macro header
  homePageUrl="/"
  serviceUrl="/"
  headerClasses=""
  containerClasses=""
  topNavigation=false
  logoText=""
  logoProductText=""
  headerNav=false
  serviceName=""
  wrapperWidth=false
  headerLogo=""
>
  <@fdsHeader.header
    homePageUrl=homePageUrl
    serviceUrl=serviceUrl
    headerClasses=headerClasses
    containerClasses=containerClasses
    topNavigation=topNavigation
    logoText=logoText
    logoProductText=logoProductText
    headerNav=headerNav
    serviceName=serviceName
    wrapperWidth=wrapperWidth
    headerLogo=headerLogo
  >
    <@fdsHeader.headerNavigation>
      <#if currentUserView?has_content && currentUserView.isAuthenticated()>
        <@fdsHeader.headerNavigationItem
          itemText=currentUserView.fullName
          itemActive=false
        />
        <@fdsHeader.headerNavigationSignOutButton
          buttonText="Sign out"
          formUrl=springUrl("/logout")
        />
      </#if>
    </@fdsHeader.headerNavigation>
  </@fdsHeader.header>
</#macro>