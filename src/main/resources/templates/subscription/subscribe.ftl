<#include '../layout.ftl'>

<#assign pageHeading = "Subscribe to newsletter">

<@defaultPage htmlTitle=pageHeading pageHeading=pageHeading breadcrumbs=false topNavigation=false>
  <#if errorList?has_content>
    <@fdsError.errorSummary errorItems=errorList />
  </#if>

  <@fdsInsetText.insetText>
    Subscribing to the pathfinder newsletter will result in you receiving an email once a month detailing the projects which have been created or updated
  </@fdsInsetText.insetText>

  <@fdsForm.htmlForm>
    <@fdsTextInput.textInput path="form.forename" labelText="First name" />
    <@fdsTextInput.textInput path="form.surname" labelText="Last name" />
    <@fdsTextInput.textInput path="form.emailAddress" labelText="Email address" />
    <@fdsRadio.radioGroup
      labelText="Relation to Pathfinder"
      path="form.relationToPathfinder"
      hiddenContent=true
    >
      <@fdsRadio.radioItem path="form.relationToPathfinder" itemMap=supplyChainRelation isFirstItem=true />
      <@fdsRadio.radioItem path="form.relationToPathfinder" itemMap=operatorRelation />
      <@fdsRadio.radioItem path="form.relationToPathfinder" itemMap=otherRelation>
        <@fdsTextarea.textarea
          path="form.subscribeReason"
          labelText="Reason for subscribing"
          nestingPath="form.relationToPathfinder"
        />
      </@fdsRadio.radioItem>
    </@fdsRadio.radioGroup>
    <@fdsAction.button buttonText="Subscribe"/>
  </@fdsForm.htmlForm>
</@defaultPage>
