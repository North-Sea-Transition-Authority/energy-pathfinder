<#include 'layout.ftl'/>
<#include 'fds/components/header/energyPortalHeader.ftl'/>

<#-- @ftlvariable name="currentUserView" type="uk.co.ogauthority.pathfinder.auth.CurrentUserView" -->

<#macro header wrapperWidth=false>
  <@energyPortalHeader
    headerLogo="NSTA"
    userDisplayName=(currentUserView?has_content && currentUserView.isAuthenticated())?then(currentUserView.fullName, "")
    wrapperWidth=wrapperWidth
    signOutUrl=springUrl("/logout")
  />
</#macro>