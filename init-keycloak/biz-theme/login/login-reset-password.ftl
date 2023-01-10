<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true displayMessage=!messagesPerField.existsError('username'); section>
    <#if section = "header">
      <div class="mb-6"><h3 class="mb-1">Forgot Password</h3>
        <p>Please enter your email address to receive a verification code</p></div>
    <#elseif section = "form">
      <form action="${url.loginAction}" method="post">
        <div class="form-container vertical">
          <div class="">
            <div class="form-item vertical"><label class="form-label"></label>
              <div class=""><input
                        class="input input-md h-11 focus:ring-indigo-600 focus-within:ring-indigo-600 focus-within:border-indigo-600 focus:border-indigo-600"
                        type="text" name="username" autocomplete="off" placeholder="Email" value="admin@mail.com">
              </div>
            </div>
          </div>
          <button class="button bg-indigo-600 hover:bg-indigo-500 active:bg-indigo-700 text-white radius-round h-11 px-8 py-2 w-full"
                  type="submit">Send Email
          </button>
        </div>
      </form>
    <#elseif section = "info" >
      <div class="mt-4 text-center"><span>Back to </span><a class="text-indigo-600 hover:underline"
                                                            href="${url.loginUrl}">Sign
          in</a></div>
    </#if>
</@layout.registrationLayout>
