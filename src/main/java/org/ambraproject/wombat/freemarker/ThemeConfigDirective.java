package org.ambraproject.wombat.freemarker;

import freemarker.core.Environment;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
import org.ambraproject.wombat.config.theme.Theme;
import org.ambraproject.wombat.controller.SiteResolver;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

/**
 * Reads a theme config value into a FreeMarker variable. The param {@code "map"} is the name of the theme config map to
 * read, and the param {@code "value"} is the key of the value to read from the map.
 * <p/>
 * Example invocation:
 * <pre>
 *   <@themeConfig map="widget" value="widgetCount" ; c>
 *     You have ${c}
 *     <#if c == 1>widget<#else>widgets</#if>
 *   </@themeConfig>
 * </pre>
 * This would read the {@code widget.yaml} (or {@code widget.json}) from the current page's theme, extract the {@code
 * "widgetCount"} value from that map, and set that value as {@code c} for the interior of the body.
 */
public class ThemeConfigDirective extends VariableLookupDirective<Object> {

  @Autowired
  private SiteResolver siteResolver;

  @Override
  protected Object getValue(Environment env, Map params) throws TemplateException, IOException {
    Object mapNameObj = params.get("map");
    if (mapNameObj == null) throw new TemplateModelException("map param required");

    Object valueNameObj = params.get("value");
    if (valueNameObj == null) throw new TemplateModelException("value param required");

    Theme theme = new SitePageContext(siteResolver, env).getSite().getTheme();
    Map<String, Object> configMap = theme.getConfigMap(mapNameObj.toString());
    if (configMap == null) {
      throw new TemplateModelException("No config map exists for: " + mapNameObj);
    }

    return configMap.get(valueNameObj.toString());
  }

}
