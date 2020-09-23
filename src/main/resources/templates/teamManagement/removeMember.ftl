<#include '../layout.ftl'>

<@defaultPage htmlTitle="Remove User" pageHeading="Are you sure you want to remove this user from the ${teamName} team?" backLink=true topNavigation=showTopNav twoThirdsColumn=false>
  <#if error?has_content>
    <@fdsError.singleErrorSummary errorMessage=error/>
  </#if>
  <@fdsForm.htmlForm>
    <@fdsCheckAnswers.checkAnswers summaryListId="remove-member">
      <@checkAnswers.checkAnswersRowNoActions prompt="Full name" value=teamMember.fullName!""/>
      <@checkAnswers.checkAnswersRowNoActions prompt="Email address" value=teamMember.emailAddress!""/>
      <@checkAnswers.checkAnswersRowNoActions prompt="Telephone number" value=teamMember.telephoneNo!""/>

      <#assign roles>
        <#list teamMember.roleViews?sort_by("displaySequence") as role>
          ${role.title}<#if role_has_next>,</#if>
        </#list>
      </#assign>
      <@checkAnswers.checkAnswersRowNoActions prompt="Roles" value=roles/>

    </@fdsCheckAnswers.checkAnswers>

    <@fdsAction.submitButtons
      primaryButtonText="Remove"
      primaryButtonClass="govuk-button govuk-button--warning"
      secondaryLinkText="Cancel"
      linkSecondaryAction=true
      linkSecondaryActionUrl=springUrl(cancelUrl)
    />
  </@fdsForm.htmlForm>
</@defaultPage>