<#include '../../layout.ftl'>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem>" -->
<#-- @ftlvariable name="pageTitle" type="String" -->
<#-- @ftlvariable name="publishedProjectRestUrl" type="String" -->
<#-- @ftlvariable name="projectTypeDisplayNameLowercase" type="String" -->
<#-- @ftlvariable name="projectTypeDisplayName" type="String" -->
<#-- @ftlvariable name="service" type="uk.co.ogauthority.pathfinder.config.ServiceProperties" -->
<#-- @ftlvariable name="alreadyAddedProjects" type="java.util.List<uk.co.ogauthority.pathfinder.model.view.campaignInformation.CampaignProjectView>" -->

<@defaultPage htmlTitle=pageTitle pageHeading=pageTitle breadcrumbs=true errorItems=errorList>
  <@fdsForm.htmlForm>
    <@fdsTextarea.textarea path="form.scopeDescription" labelText="What is the scope of the campaign?"/>
    <@fdsDetails.summaryDetails summaryTitle="What should I provide for the scope of the campaign?">
      <p class="govuk-body">For all the assets involved in the campaign include:</p>
      <ul class="govuk-list govuk-list--bullet">
        <li>the number of wells to be drilled or decommissioned</li>
        <li>a description of infrastructure to be installed or removed</li>
        <li>any logistics sharing</li>
        <li>any pipeline installation or removal</li>
      </ul>
    </@fdsDetails.summaryDetails>

    <#assign partOfPublishedCampaignFormBinding = "form.isPartOfCampaign"/>

    <@fdsRadio.radioGroup
      labelText="Is this ${projectTypeDisplayNameLowercase} already part of a campaign with a published ${service.serviceName} ${projectTypeDisplayNameLowercase}?"
      path=partOfPublishedCampaignFormBinding
      hiddenContent=true
    >
      <@fdsRadio.radioYes path=partOfPublishedCampaignFormBinding>
        <h3 class="govuk-heading-m">Campaign ${projectTypeDisplayNameLowercase}s</h3>
        <@fdsAddToList.addToList
          pathForList="form.projectsIncludedInCampaign"
          pathForSelector="form.projectSelect"
          alreadyAdded=alreadyAddedProjects
          title=""
          itemName=projectTypeDisplayName
          noItemText="No ${projectTypeDisplayNameLowercase}s added"
          invalidItemText="This ${projectTypeDisplayNameLowercase} is no longer a published ${projectTypeDisplayNameLowercase}"
          addToListId="campaign-projects-table"
          selectorLabelText="Add an ${service.serviceName} ${projectTypeDisplayNameLowercase}"
          selectorHintText="You can add more than one ${projectTypeDisplayNameLowercase}"
          restUrl=springUrl(publishedProjectRestUrl)
          selectorNestingPath=partOfPublishedCampaignFormBinding
          selectorFormGroupClass="govuk-!-margin-bottom-5"
        />
        <@fdsDetails.summaryDetails summaryTitle="The ${projectTypeDisplayNameLowercase} I want to add is not in the list">
          <p class="govuk-body">
            You can only add ${service.serviceName} ${projectTypeDisplayNameLowercase}s that have been published
            on the ${service.customerMnemonic}
            <@fdsAction.link
              linkText="supply chain interface"
              linkUrl=service.supplyChainInterfaceUrl
              openInNewTab=true
            />
          </p>
        </@fdsDetails.summaryDetails>
      </@fdsRadio.radioYes>
      <@fdsRadio.radioNo path=partOfPublishedCampaignFormBinding/>
    </@fdsRadio.radioGroup>
    <@fdsAction.submitButtons
      primaryButtonText="Save and complete"
      secondaryButtonText="Save and complete later"
    />
  </@fdsForm.htmlForm>
</@defaultPage>
