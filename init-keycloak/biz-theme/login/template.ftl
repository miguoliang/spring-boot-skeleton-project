<#macro registrationLayout bodyClass="" displayInfo=false displayMessage=true displayRequiredFields=false>
  <!DOCTYPE html>
  <html class="${properties.kcHtmlClass!}" dir="ltr">

  <head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="robots" content="noindex, nofollow">
      <#if properties.meta?has_content>
          <#list properties.meta?split(' ') as meta>
            <meta name="${meta?split('==')[0]}" content="${meta?split('==')[1]}"/>
          </#list>
      </#if>
    <title>${msg("loginTitle",(realm.displayName!''))}</title>
    <link rel="icon" href="${url.resourcesPath}/img/favicon.ico"/>
      <#if properties.stylesCommon?has_content>
          <#list properties.stylesCommon?split(' ') as style>
            <link href="${url.resourcesCommonPath}/${style}" rel="stylesheet"/>
          </#list>
      </#if>
      <#if properties.styles?has_content>
          <#list properties.styles?split(' ') as style>
            <link href="${url.resourcesPath}/${style}" rel="stylesheet"/>
          </#list>
      </#if>
      <#if properties.scripts?has_content>
          <#list properties.scripts?split(' ') as script>
            <script src="${url.resourcesPath}/${script}" type="text/javascript"></script>
          </#list>
      </#if>
      <#if scripts??>
          <#list scripts as script>
            <script src="${script}" type="text/javascript"></script>
          </#list>
      </#if>
  </head>

  <body class="${properties.kcBodyClass!}">
  <div class="${properties.kcLoginClass!}">
    <div class="app-layout-blank flex flex-auto flex-col h-[100vh]">
      <div class="grid lg:grid-cols-3 h-full">
        <div class="bg-no-repeat bg-cover py-6 px-16 flex-col justify-between hidden lg:flex"
             style="background-image: url(&quot;${url.resourcesPath}/img/others/auth-side-bg.jpg&quot;);">
          <div class="logo" style="width: auto;"><img src="${url.resourcesPath}/img/logo/logo-dark-full.png"
                                                      alt="Elstar logo"></div>
          <div>
            <div class="mb-6 flex items-center gap-4"><span
                      class="avatar avatar-circle avatar-md border-2 border-white"><img class="avatar-img avatar-circle"
                                                                                        src="${url.resourcesPath}/img/avatars/thumb-10.jpg"
                                                                                        loading="lazy"></span>
              <div class="text-white">
                <div class="font-semibold text-base">Brittany Hale</div>
                <span class="opacity-80">CTO, Onward</span></div>
            </div>
            <p class="text-lg text-white opacity-80">Elstar comes with a complete set of UI components crafted with
              Tailwind CSS, it fulfilled most of the use case to create modern and beautiful UI and application</p>
          </div>
          <span class="text-white">Copyright  ©  2023 <span class="font-semibold">Elstar</span> </span></div>
        <div class="col-span-2 flex flex-col justify-center items-center bg-white dark:bg-gray-800">
          <div class="xl:min-w-[450px] px-8">

              <#nested "header">

              <#-- App-initiated actions should not see warning messages about the need to complete the action -->
              <#-- during login.                                                                               -->
              <#if displayMessage && message?has_content && (message.type != 'warning' || !isAppInitiatedAction??)>
              <#if message.type = 'error'>
                  <#assign alertColor = 'red'>
              </#if>
              <#if message.type = 'info'>
                  <#assign alertColor = 'blue'>
              </#if>
              <#if message.type='success'>
                  <#assign alertColor = 'green'>
              </#if>
              <#if message.type = 'warning'>
                  <#assign alertColor = 'yellow'>
              </#if>
            <div class="alert p-4 relative flex bg-${alertColor}-50 dark:bg-${alertColor}-500 text-${alertColor}-500 dark:text-${alertColor}-50 font-semibold rounded-lg mb-8">
              <div class="flex items-center">
                  <#if message.type = 'success'><span class="text-2xl text-emerald-400 dark:text-emerald-50"><svg
                            stroke="currentColor" fill="currentColor" stroke-width="0" viewBox="0 0 20 20"
                            height="1em" width="1em" xmlns="http://www.w3.org/2000/svg"><path fill-rule="evenodd"
                                                                                              d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                                                                                              clip-rule="evenodd"></path></svg></span></#if>
                  <#if message.type = 'warning'>
                    <span class="text-2xl text-yellow-400 dark:text-yellow-50"><svg stroke="currentColor"
                                                                                    fill="currentColor"
                                                                                    stroke-width="0"
                                                                                    viewBox="0 0 20 20" height="1em"
                                                                                    width="1em"
                                                                                    xmlns="http://www.w3.org/2000/svg"><path
                                fill-rule="evenodd"
                                d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z"
                                clip-rule="evenodd"></path></svg></span>
                  </#if>
                  <#if message.type = 'error'><span class="text-2xl text-red-400 dark:text-red-100"><svg
                            stroke="currentColor" fill="currentColor" stroke-width="0" viewBox="0 0 20 20"
                            height="1em" width="1em" xmlns="http://www.w3.org/2000/svg"><path fill-rule="evenodd"
                                                                                              d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
                                                                                              clip-rule="evenodd"></path></svg></span></#if>
                  <#if message.type = 'info'><span class="text-2xl text-blue-400 dark:text-blue-100"><svg
                            stroke="currentColor" fill="currentColor" stroke-width="0" viewBox="0 0 20 20"
                            height="1em" width="1em" xmlns="http://www.w3.org/2000/svg"><path fill-rule="evenodd"
                                                                                              d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z"
                                                                                              clip-rule="evenodd"></path></svg></span></#if>
                <div class="ltr:ml-2 rtl:mr-2">${kcSanitize(message.summary)?no_esc}</div>
              </div>
            </div>
          </div>
            </#if>
            <#nested "form">
            <#nested "socialProviders">
            <#nested "info">
        </div>
      </div>
    </div>
  </div>
  </body>
  </html>
</#macro>
