<#include '../../layout.ftl'>

<#macro minimumRequirementNotMetInset itemRequiredText linkUrl>
  <@fdsInsetText.insetText>
    <p>
      Your project requires at least one ${itemRequiredText} as you advised they would be provided in the 'Set up your project' section.
    </p>
    <p>
      <@fdsAction.link linkText="Change your project set up" linkUrl=linkUrl/>
    </p>
  </@fdsInsetText.insetText>
</#macro>