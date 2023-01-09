<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('firstName','lastName','email','username','password','password-confirm'); section>
    <#if section = "header">
      <div class="mb-8"><h3 class="mb-1">Sign Up</h3>
        <p>And lets get started with your free trial</p></div>
    <#elseif section = "form">
      <form action="#">
        <div class="form-container vertical">
          <div class="form-item vertical"><label class="form-label mb-2">User Name</label>
            <div class=""><input
                      class="input input-md h-11 focus:ring-indigo-600 focus-within:ring-indigo-600 focus-within:border-indigo-600 focus:border-indigo-600"
                      type="text" name="userName" autocomplete="off" placeholder="User Name" value="admin1"></div>
          </div>
          <div class="form-item vertical"><label class="form-label mb-2">Email</label>
            <div class=""><input
                      class="input input-md h-11 focus:ring-indigo-600 focus-within:ring-indigo-600 focus-within:border-indigo-600 focus:border-indigo-600"
                      type="email" name="email" autocomplete="off" placeholder="Email" value="test@testmail.com"></div>
          </div>
          <div class="form-item vertical"><label class="form-label mb-2">Password</label>
            <div class=""><span class="input-wrapper "><input
                        class="input input-md h-11 focus:ring-indigo-600 focus-within:ring-indigo-600 focus-within:border-indigo-600 focus:border-indigo-600"
                        type="password" name="password" autocomplete="off" placeholder="Password" value="123Qwe1"
                        style="padding-right: 2.25rem;"><div class="input-suffix-end"><span
                          class="cursor-pointer text-xl"><svg stroke="currentColor" fill="none" stroke-width="0"
                                                              viewBox="0 0 24 24" height="1em" width="1em"
                                                              xmlns="http://www.w3.org/2000/svg"><path
                              stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"></path></svg></span></div></span>
            </div>
          </div>
          <div class="form-item vertical"><label class="form-label mb-2">Confirm Password</label>
            <div class=""><span class="input-wrapper "><input
                        class="input input-md h-11 focus:ring-indigo-600 focus-within:ring-indigo-600 focus-within:border-indigo-600 focus:border-indigo-600"
                        type="password" name="confirmPassword" autocomplete="off" placeholder="Confirm Password"
                        value="123Qwe1" style="padding-right: 2.25rem;"><div class="input-suffix-end"><span
                          class="cursor-pointer text-xl"><svg stroke="currentColor" fill="none" stroke-width="0"
                                                              viewBox="0 0 24 24" height="1em" width="1em"
                                                              xmlns="http://www.w3.org/2000/svg"><path
                              stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"></path></svg></span></div></span>
            </div>
          </div>
          <button class="button bg-indigo-600 hover:bg-indigo-500 active:bg-indigo-700 text-white radius-round h-11 px-8 py-2 w-full"
                  type="submit">Sign Up
          </button>
          <div class="mt-4 text-center"><span>Already have an account? </span><a class="text-indigo-600 hover:underline"
                                                                                 href="/sign-in">Sign in</a></div>
        </div>
      </form>
    <#--        <form id="kc-register-form" class="${properties.kcFormClass!}" action="${url.registrationAction}" method="post">-->
    <#--            <div class="${properties.kcFormGroupClass!}">-->
    <#--                <div class="${properties.kcLabelWrapperClass!}">-->
    <#--                    <label for="firstName" class="${properties.kcLabelClass!}">${msg("firstName")}</label>-->
    <#--                </div>-->
    <#--                <div class="${properties.kcInputWrapperClass!}">-->
    <#--                    <input type="text" id="firstName" class="${properties.kcInputClass!}" name="firstName"-->
    <#--                           value="${(register.formData.firstName!'')}"-->
    <#--                           aria-invalid="<#if messagesPerField.existsError('firstName')>true</#if>"-->
    <#--                    />-->

    <#--                    <#if messagesPerField.existsError('firstName')>-->
    <#--                        <span id="input-error-firstname" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">-->
    <#--                            ${kcSanitize(messagesPerField.get('firstName'))?no_esc}-->
    <#--                        </span>-->
    <#--                    </#if>-->
    <#--                </div>-->
    <#--            </div>-->

    <#--            <div class="${properties.kcFormGroupClass!}">-->
    <#--                <div class="${properties.kcLabelWrapperClass!}">-->
    <#--                    <label for="lastName" class="${properties.kcLabelClass!}">${msg("lastName")}</label>-->
    <#--                </div>-->
    <#--                <div class="${properties.kcInputWrapperClass!}">-->
    <#--                    <input type="text" id="lastName" class="${properties.kcInputClass!}" name="lastName"-->
    <#--                           value="${(register.formData.lastName!'')}"-->
    <#--                           aria-invalid="<#if messagesPerField.existsError('lastName')>true</#if>"-->
    <#--                    />-->

    <#--                    <#if messagesPerField.existsError('lastName')>-->
    <#--                        <span id="input-error-lastname" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">-->
    <#--                            ${kcSanitize(messagesPerField.get('lastName'))?no_esc}-->
    <#--                        </span>-->
    <#--                    </#if>-->
    <#--                </div>-->
    <#--            </div>-->

    <#--            <div class="${properties.kcFormGroupClass!}">-->
    <#--                <div class="${properties.kcLabelWrapperClass!}">-->
    <#--                    <label for="email" class="${properties.kcLabelClass!}">${msg("email")}</label>-->
    <#--                </div>-->
    <#--                <div class="${properties.kcInputWrapperClass!}">-->
    <#--                    <input type="text" id="email" class="${properties.kcInputClass!}" name="email"-->
    <#--                           value="${(register.formData.email!'')}" autocomplete="email"-->
    <#--                           aria-invalid="<#if messagesPerField.existsError('email')>true</#if>"-->
    <#--                    />-->

    <#--                    <#if messagesPerField.existsError('email')>-->
    <#--                        <span id="input-error-email" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">-->
    <#--                            ${kcSanitize(messagesPerField.get('email'))?no_esc}-->
    <#--                        </span>-->
    <#--                    </#if>-->
    <#--                </div>-->
    <#--            </div>-->

    <#--            <#if !realm.registrationEmailAsUsername>-->
    <#--                <div class="${properties.kcFormGroupClass!}">-->
    <#--                    <div class="${properties.kcLabelWrapperClass!}">-->
    <#--                        <label for="username" class="${properties.kcLabelClass!}">${msg("username")}</label>-->
    <#--                    </div>-->
    <#--                    <div class="${properties.kcInputWrapperClass!}">-->
    <#--                        <input type="text" id="username" class="${properties.kcInputClass!}" name="username"-->
    <#--                               value="${(register.formData.username!'')}" autocomplete="username"-->
    <#--                               aria-invalid="<#if messagesPerField.existsError('username')>true</#if>"-->
    <#--                        />-->

    <#--                        <#if messagesPerField.existsError('username')>-->
    <#--                            <span id="input-error-username" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">-->
    <#--                                ${kcSanitize(messagesPerField.get('username'))?no_esc}-->
    <#--                            </span>-->
    <#--                        </#if>-->
    <#--                    </div>-->
    <#--                </div>-->
    <#--            </#if>-->

    <#--            <#if passwordRequired??>-->
    <#--                <div class="${properties.kcFormGroupClass!}">-->
    <#--                    <div class="${properties.kcLabelWrapperClass!}">-->
    <#--                        <label for="password" class="${properties.kcLabelClass!}">${msg("password")}</label>-->
    <#--                    </div>-->
    <#--                    <div class="${properties.kcInputWrapperClass!}">-->
    <#--                        <input type="password" id="password" class="${properties.kcInputClass!}" name="password"-->
    <#--                               autocomplete="new-password"-->
    <#--                               aria-invalid="<#if messagesPerField.existsError('password','password-confirm')>true</#if>"-->
    <#--                        />-->

    <#--                        <#if messagesPerField.existsError('password')>-->
    <#--                            <span id="input-error-password" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">-->
    <#--                                ${kcSanitize(messagesPerField.get('password'))?no_esc}-->
    <#--                            </span>-->
    <#--                        </#if>-->
    <#--                    </div>-->
    <#--                </div>-->

    <#--                <div class="${properties.kcFormGroupClass!}">-->
    <#--                    <div class="${properties.kcLabelWrapperClass!}">-->
    <#--                        <label for="password-confirm"-->
    <#--                               class="${properties.kcLabelClass!}">${msg("passwordConfirm")}</label>-->
    <#--                    </div>-->
    <#--                    <div class="${properties.kcInputWrapperClass!}">-->
    <#--                        <input type="password" id="password-confirm" class="${properties.kcInputClass!}"-->
    <#--                               name="password-confirm"-->
    <#--                               aria-invalid="<#if messagesPerField.existsError('password-confirm')>true</#if>"-->
    <#--                        />-->

    <#--                        <#if messagesPerField.existsError('password-confirm')>-->
    <#--                            <span id="input-error-password-confirm" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">-->
    <#--                                ${kcSanitize(messagesPerField.get('password-confirm'))?no_esc}-->
    <#--                            </span>-->
    <#--                        </#if>-->
    <#--                    </div>-->
    <#--                </div>-->
    <#--            </#if>-->

    <#--            <#if recaptchaRequired??>-->
    <#--                <div class="form-group">-->
    <#--                    <div class="${properties.kcInputWrapperClass!}">-->
    <#--                        <div class="g-recaptcha" data-size="compact" data-sitekey="${recaptchaSiteKey}"></div>-->
    <#--                    </div>-->
    <#--                </div>-->
    <#--            </#if>-->

    <#--            <div class="${properties.kcFormGroupClass!}">-->
    <#--                <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">-->
    <#--                    <div class="${properties.kcFormOptionsWrapperClass!}">-->
    <#--                        <span><a href="${url.loginUrl}">${kcSanitize(msg("backToLogin"))?no_esc}</a></span>-->
    <#--                    </div>-->
    <#--                </div>-->

    <#--                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">-->
    <#--                    <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" type="submit" value="${msg("doRegister")}"/>-->
    <#--                </div>-->
    <#--            </div>-->
    <#--        </form>-->
    </#if>
</@layout.registrationLayout>