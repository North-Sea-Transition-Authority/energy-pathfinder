<#include '../layoutPane.ftl'>

<#macro defaultPageWithSidebar pageHeading themeHeading sidebarSectionLinks htmlTitle=pageHeading isSidebarSticky=false>
  <@defaultPagePane htmlTitle=htmlTitle phaseBanner=false>
    <@defaultPagePaneSubNav>
      <@fdsSubNavigation.subNavigation sticky=isSidebarSticky>
        <@fdsSubNavigation.subNavigationSection themeHeading=themeHeading>
          <#list sidebarSectionLinks as sidebarLink>
            <@sideBarSectionLink.renderSidebarLink sidebarLink=sidebarLink/>
          </#list>
        </@fdsSubNavigation.subNavigationSection>
      </@fdsSubNavigation.subNavigation>
    </@defaultPagePaneSubNav>

    <@defaultPagePaneContent pageHeading=pageHeading!"">
      <#nested>
    </@defaultPagePaneContent>
  </@defaultPagePane>
</#macro>
