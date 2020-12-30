<#include '../layoutPane.ftl'>

<#macro defaultPageWithSidebar pageHeading themeHeading sidebarSectionLinks>
  <@defaultPagePane htmlTitle=pageHeading phaseBanner=false>
    <@defaultPagePaneSubNav>
      <@fdsSubNavigation.subNavigation>
        <@fdsSubNavigation.subNavigationSection themeHeading=themeHeading>
          <#list sidebarSectionLinks as sidebarLink>
            <@sideBarSectionLink.renderSidebarLink sidebarLink=sidebarLink/>
          </#list>
        </@fdsSubNavigation.subNavigationSection>
      </@fdsSubNavigation.subNavigation>
    </@defaultPagePaneSubNav>

    <@defaultPagePaneContent pageHeading=pageHeading>
      <#nested>
    </@defaultPagePaneContent>
  </@defaultPagePane>
</#macro>
