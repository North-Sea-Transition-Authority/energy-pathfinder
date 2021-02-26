<#include '../layout.ftl'>

<#assign pageHeading = "Subscribed" />

<@defaultPage htmlTitle=pageHeading pageHeading="" breadcrumbs=false topNavigation=false>
  <@fdsPanel.panel
    panelTitle=pageHeading
    panelText="You have been subscribed to the ${service.serviceName} newsletter"
  />
</@defaultPage>
