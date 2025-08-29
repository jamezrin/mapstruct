<#--

    Copyright MapStruct Authors.

    Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0

-->
<#-- @ftlvariable name="" type="org.mapstruct.ap.internal.model.ReferenceMappingMethod" -->
<#list annotations as annotation>
    <#nt><@includeModel object=annotation/>
</#list>
<#if overridden>@Override</#if>
<#lt>${accessibility.keyword} <@includeModel object=returnType/> ${name}(<#list parameters as param><@includeModel object=param/><#if param_has_next>, </#if></#list>)<@throws/> {
<#-- @ReferenceMapping always delegates to the resolved method -->
<#if returnType.name != "void">
    return ${delegateCallString};
<#else>
    ${delegateCallString};
</#if>
}
<#macro throws>
    <#if (thrownTypes?size > 0)><#lt> throws </#if><@compress single_line=true>
    <#list thrownTypes as exceptionType>
        <@includeModel object=exceptionType/>
        <#if exceptionType_has_next>, </#if><#t>
    </#list>
</@compress>
</#macro>
