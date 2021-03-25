<#include '../layout.ftl'>

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle topNavigation=true fullWidthColumn=true breadcrumbs=true>
  <@fdsForm.htmlForm springUrl(addCommunicationUrl)>
    <@fdsAction.button buttonText="Send new email" start=false buttonClass="govuk-button govuk-button--blue" />
  </@fdsForm.htmlForm>
  <@_communicationsList communicationViewList=sentCommunicationViews />
</@defaultPage>

<#macro _communicationsList communicationViewList>
  <#if communicationViewList?has_content>
    <ol class="govuk-list communication-list">
      <#list communicationViewList as communicationView>
        <li class="govuk-list__item communication-list__item">
          <@_communicationItem communicationView=communicationView />
        </li>
      </#list>
    </ol>
    <#else>
      <@fdsInsetText.insetText>
        <p class="govuk-body">No communications have been sent</p>
      </@fdsInsetText.insetText>
  </#if>
</#macro>

<#macro _communicationItem communicationView>
  <div class="communication-item">
    <h3 class="communication-item__heading">
      <@fdsAction.link
        linkText="${communicationView.emailView.subject}"
        linkUrl=springUrl(communicationView.communicationUrl)
        linkClass="govuk-link govuk-!-font-size-24 govuk-link--no-visited-state"
      />
    </h3>
    <span class="govuk-caption-m">${communicationView.recipientType!""}</span>
    <#assign recipients>
      <@_recipientSummary communicationView=communicationView />
    </#assign>
    <@fdsDataItems.dataItem dataItemListClasses="communication-item__data-list" >
      <@fdsDataItems.dataValues key="Recipients" value="${recipients}"/>
      <@fdsDataItems.dataValues key="Date sent" value=communicationView.formattedDateSent!""/>
      <@fdsDataItems.dataValues key="Sent by" value=communicationView.submittedByUserName!""/>
    </@fdsDataItems.dataItem>
  </div>
</#macro>

<#macro _recipientSummary communicationView>
  <#if communicationView.operatorRecipientType>
    <#assign recipientListSize=communicationView.emailView.recipientList?size/>
    <span>${recipientListSize} ${textUtil.pluralise(recipientListSize, "operator", "operators")}</span>
    <#elseif communicationView.subscriberRecipientType>
      <span>${service.serviceName} subscribers</span>
  </#if>
</#macro>