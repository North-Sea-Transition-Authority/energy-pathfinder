<#include '../../layoutPane.ftl'/>

<#macro summary pageHeading projectSummaryView sidebarHeading>
  <@defaultPagePaneSubNav>
    <@fdsSubNavigation.subNavigation>
      <@fdsSubNavigation.subNavigationSection themeHeading=sidebarHeading>
        <#list projectSummaryView.sidebarSectionLinks as sidebarLink>
          <@sideBarSectionLink.renderSidebarLink sidebarLink=sidebarLink/>
        </#list>
      </@fdsSubNavigation.subNavigationSection>
    </@fdsSubNavigation.subNavigation>
  </@defaultPagePaneSubNav>

  <@defaultPagePaneContent pageHeading=pageHeading>
    ${projectSummaryView.summaryHtml?no_esc}
    <#nested>
  </@defaultPagePaneContent>
</#macro>