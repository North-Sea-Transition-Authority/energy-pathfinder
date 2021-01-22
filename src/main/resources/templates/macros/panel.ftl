<#macro panelSection headingText>
  <h3 class="fds-contact-panel__heading fds-contact-panel__heading--m">${headingText}</h3>
  <div class="panel__body fds-contact-panel__body">
    <#nested>
  </div>
</#macro>

<#macro panel headingText headingSize="h2" headingClass="fds-contact-panel__heading fds-contact-panel__heading--l">
  <div class="fds-contact-panel fds-contact-panel--top">
    <${headingSize} class="${headingClass}">
      ${headingText}
    </${headingSize}>
    <div class="fds-contact-panel__content">
      <#nested>
    </div>
  </div>
</#macro>
