<#include '../layout.ftl'>
<#import '_subscribeSummary.ftl' as subscribe>

<#assign serviceName = service.serviceName>
<#assign pageHeading = "Subscribe to ${serviceName}">

<@defaultPage htmlTitle=pageHeading pageHeading=pageHeading breadcrumbs=false topNavigation=false errorItems=errorList pageHeadingClass="govuk-heading-l">

  <@subscribe._subscriptionSummaryText pronoun="Subscribers"/>

  <@fdsForm.htmlForm>
    <@fdsTextInput.textInput path="form.forename" labelText="First name" />
    <@fdsTextInput.textInput path="form.surname" labelText="Last name" />
    <@fdsTextInput.textInput path="form.emailAddress" labelText="Email address" />
    <@fdsRadio.radioGroup
      labelText="Relation to ${serviceName}"
      path="form.relationToPathfinder"
      hiddenContent=true
    >
      <@fdsRadio.radioItem path="form.relationToPathfinder" itemMap=developerRelation isFirstItem=true />
      <@fdsRadio.radioItem path="form.relationToPathfinder" itemMap=operatorRelation />
      <@fdsRadio.radioItem path="form.relationToPathfinder" itemMap=supplyChainRelation />
      <@fdsRadio.radioItem path="form.relationToPathfinder" itemMap=otherRelation>
        <@fdsTextarea.textarea
          path="form.subscribeReason"
          labelText="Describe your relation to ${serviceName}"
          nestingPath="form.relationToPathfinder"
        />
      </@fdsRadio.radioItem>
    </@fdsRadio.radioGroup>
    <@fdsAction.button buttonText="Subscribe"/>
  </@fdsForm.htmlForm>
</@defaultPage>