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
              <#nested "form">
              <#nested "socialProviders">
              <#nested "info">
          </div>
        </div>
      </div>

    </div>
  </div>
  </body>
  </html>
</#macro>
