<#include '../layout.ftl'>
<#import '_subscribeSummary.ftl' as subscribe>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem>" -->
<#-- @ftlvariable name="service" type="uk.co.ogauthority.pathfinder.config.ServiceProperties" -->
<#-- @ftlvariable name="developerRelation" type="uk.co.ogauthority.pathfinder.model.enums.subscription.RelationToPathfinder" -->
<#-- @ftlvariable name="operatorRelation" type="uk.co.ogauthority.pathfinder.model.enums.subscription.RelationToPathfinder" -->
<#-- @ftlvariable name="supplyChainRelation" type="uk.co.ogauthority.pathfinder.model.enums.subscription.RelationToPathfinder" -->
<#-- @ftlvariable name="otherRelation" type="uk.co.ogauthority.pathfinder.model.enums.subscription.RelationToPathfinder" -->

<#assign serviceName = service.serviceName>
<#assign pageHeading = "Subscribe to ${serviceName}">

<@defaultPage
  htmlTitle=pageHeading
  pageHeading=pageHeading
  breadcrumbs=false
  topNavigation=false
  errorItems=errorList
  pageHeadingClass="govuk-heading-l"
  phaseBanner=false
>

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