<#include "../layout.ftl">

<@defaultPage htmlTitle=teamName backLink=!showBreadcrumbs pageHeading=teamName topNavigation=showTopNav twoThirdsColumn=false breadcrumbs=showBreadcrumbs>

  <#if additionalGuidanceText??>
    <p class="govuk-body">
      ${additionalGuidanceText}
    </p>
  </#if>

  <#if allRoles??>
    <@fdsDetails.summaryDetails summaryTitle="What does each role allow a user to do?" >
      <@fdsCheckAnswers.checkAnswers summaryListClass="">
        <#list allRoles as propName, propValue>
          <#assign description = propValue?keep_before("(") >
          <@fdsCheckAnswers.checkAnswersRow keyText="${propName}" actionText="" actionUrl="" screenReaderActionText="">
            ${description}
          </@fdsCheckAnswers.checkAnswersRow>
        </#list>
      </@fdsCheckAnswers.checkAnswers>
    </@fdsDetails.summaryDetails>
  </#if>

  <@userAction.userAction userAction=addUserAction />

  <#list teamMemberViews>
    <table class="govuk-table">
      <thead class="govuk-table__head">
      <tr class="govuk-table__row">
        <th class="govuk-table__header" scope="col">Name</th>
        <th class="govuk-table__header" scope="col">Contact details</th>
        <th class="govuk-table__header" scope="col">Roles</th>
        <#if showTeamMemberActions>
          <th class="govuk-table__header" scope="col">Actions</th>
        </#if>
      </tr>
      <tbody class="govuk-table__body">
        <#items as teamMemberView>
          <tr class="govuk-table__row">
            <td class="govuk-table__cell">
              ${teamMemberView.fullName}
            </td>
            <td class="govuk-table__cell">
              <ul class="govuk-list">
                <li>${teamMemberView.emailAddress}</li>
                <li>${teamMemberView.telephoneNo}</li>
              </ul>
            </td>
            <td class="govuk-table__cell">
              <#list teamMemberView.roleViews?sort_by("displaySequence") as roleView>
                  ${roleView.title}
                <br>
              </#list>
            </td>
            <#if showTeamMemberActions>
              <td class="govuk-table__cell">
                <ul class="govuk-list">
                  <li>
                    <@userAction.userAction userAction=teamMemberView.editAction/>
                  </li>
                  <li>
                    <@userAction.userAction userAction=teamMemberView.removeAction/>
                  </li>
                </ul>
              </td>
            </#if>
          </tr>
        </#items>
      </tbody>
    </table>
  </#list>

  <#if backUrl??>
    <@fdsAction.link linkText="Complete section" linkClass="govuk-button"  linkUrl=springUrl(backUrl)/>
  </#if>

</@defaultPage>