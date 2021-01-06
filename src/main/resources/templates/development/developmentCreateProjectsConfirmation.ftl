<#include '../layout.ftl'>


<@defaultPage htmlTitle="Create projects confirmation" pageHeading="Create projects confirmation" breadcrumbs=false>

  <h2 class="govuk-heading-m">Your projects are being created</h2>

  <p class="govuk-body">
    Check the console in IntelliJ for info on progress.
  </p>

  <@fdsAction.link linkClass="govuk-link govuk-!-font-size-19" linkText="Back to work area" linkUrl=springUrl(workAreaUrl)/>
  <br/>
  <@fdsAction.link linkClass="govuk-link govuk-!-font-size-19" linkText="Back to project create" linkUrl=springUrl(createProjectsUrl)/>
</@defaultPage>
