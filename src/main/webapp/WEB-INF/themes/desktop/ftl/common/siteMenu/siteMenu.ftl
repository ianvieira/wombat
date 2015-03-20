<#include "siteMenuFlag.ftl" />
<#if hasSiteMenu>

  <#macro siteMenuCalloutHeadline>
  <h3><#nested/></h3>
  </#macro>

  <#macro siteMenuCalloutBulletList>
  <ul>
    <#nested/>
  </ul>
  </#macro>
  <#macro siteMenuCalloutButton href>
  <a class="btn" href="${href}">
    <#nested/>
  </a>
  </#macro>

  <#macro menuGroup title singleColumn=false containsCallout=false>

    <#if singleColumn>
      <#assign column="single">
      <#nested/>
    <#else>
      <#assign column="group">
    <li class="multi-col-parent menu-section-header has-dropdown hover" id="${title?lower_case?replace(" ","-")}">
    ${title}
      <div class="dropdown mega" style="border: 1px solid">
      <ul class="multi-col"  id="${title?lower_case?replace(" ","-")}-dropdown-list">
        <#nested/>
      </ul>
      <#if containsCallout>
        <div class="calloutcontainer">
            <#include "siteMenuCallout.ftl" />
          </div>
      </#if>
      </div>
    </li>
    </#if>
  <#--</li>-->

  </#macro>
  <#macro menuSection title >

  <li class="menu-section-header <#if column="single">has-dropdown </#if>" id="${title?lower_case?replace(" ","-")}">
     ${title}

    <ul class="menu-section <#if column="single">dropdown </#if>" id="${title?lower_case?replace(" ","-")}-dropdown-list">
      <#nested/>
    </ul>

  </li>
  </#macro>


  <#macro menuLink href>
  <li>
    <a href="${href}" style="border: 1px solid; z-index: 100"><#nested/></a>
  </li>
  </#macro>

  <#macro menuSpecialSection title>
  <li data-js-tooltip-hover="trigger" class="subject-area">
  ${title}
     <#nested/>
  </li>
  </#macro>

  <#macro tooltip >
  <p data-js-tooltip-hover="target" class="subject-area-info">
    <#nested/>
  </p>
  </#macro>

<#--Markup starts here
MARKUP: using Foundation Top Bar for navigation -->
<ul class="logo">
  <li class="home-link">
    <h1><a href="<@siteLink path="." />">${siteTitle}</a></h1>
  </li>
</ul>
<section class="top-bar-section"> <#--closed in header.ftl-->

<ul class="nav-elements">
  <#include "siteMenuItems.ftl" />
</#if>

<@js src="resource/js/vendor/jquery.hoverIntent.js"/>
<@js src="resource/js/components/menu_drop.js"/>
<@js src="resource/js/components/hover_delay.js"/>
