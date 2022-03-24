<#include '../layout.ftl'>

<#macro footerLinks>
  <@fdsFooter.footerMetaLink linkText="Accessibility statement" linkUrl=springUrl(accessibilityStatementUrl)/>
  <@fdsFooter.footerMetaLink linkText="Contact" linkUrl=springUrl(contactUrl)/>
  <@fdsFooter.footerMetaLink linkText="Cookies" linkUrl=springUrl(cookiePrefsUrl)/>
  <@fdsFooter.footerMetaLink linkText="Feedback" linkUrl=springUrl(feedbackUrl)/>
</#macro>