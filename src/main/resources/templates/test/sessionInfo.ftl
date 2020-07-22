<#include '../layout.ftl'>

<@defaultPage htmlTitle="Session integration test" pageHeading="Session integration test" twoThirdsColumn=false>
  <#if user?has_content>
    <ul class="govuk-list govuk-list--bullet">
      <li>wuaId: ${user.wuaId}</li>
      <li>fname: ${user.forename}</li>
      <li>sname: ${user.surname}</li>
      <li>email: ${user.emailAddress}</li>
      <li>privs:
        <ul class="govuk-list govuk-list--bullet">
          <#list user.userPrivileges as privilege>
            <li>${privilege.name()}</li>
          </#list>
        </ul>
      </li>
    </ul>
  <#else>
    <p class="govuk-body">user is unauthenticated</p>
  </#if>
</@defaultPage>