<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
    <#if section = "header">
      <div class="mb-8"><h3 class="mb-1">${msg("emailVerifyTitle")}</h3></div>
    <#elseif section = "form">
      <p class="mb-8">${msg("emailVerifyInstruction1", user.email)}</p>
    <#elseif section = "info">
      <p class="mb-8">
          ${msg("emailVerifyInstruction2")}
        <br/>
        <a class="text-blue-500" href="${url.loginAction}">${msg("doClickHere")}</a> ${msg("emailVerifyInstruction3")}
      </p>
    </#if>
</@layout.registrationLayout>
