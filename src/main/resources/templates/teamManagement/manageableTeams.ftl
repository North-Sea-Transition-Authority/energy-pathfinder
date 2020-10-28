<#include '../layout.ftl'>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle topNavigation=true twoThirdsColumn=false backLink=true>
  <@userAction.userAction userAction=addNewOrganisationTeamAction/>
  <table class="govuk-table">
    <thead class="govuk-table__head">
    <tr class="govuk-table__row">
      <th class="govuk-table__header" scope="col">Name</th>
      <th class="govuk-table__header" scope="col">Description</th>
      <th class="govuk-table__header" scope="col">Actions</th>
    </tr>
    <tbody class="govuk-table__body">
      <#list teamViewList as team>
        <tr class="govuk-table__row">
          <td class="govuk-table__cell">
            ${team.name}
          </td>
          <td class="govuk-table__cell">
            ${team.description?has_content?then(team.description, "")}
          </td>
          <td class="govuk-table__cell">
            <@fdsAction.link
              linkUrl=springUrl(team.getSelectRoute())
              linkText=manageActionTitle
              linkScreenReaderText="${manageActionTitle} - ${team.name}"
              linkClass="govuk-link govuk-link--no-visited-state"
            />
          </td>
        </tr>
      </#list>
    </tbody>
  </table>
</@defaultPage>