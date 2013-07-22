package org.ambraproject.wombat.config;

import com.google.common.base.Preconditions;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;

/**
 * A theme defined within the webapp.
 */
public class InternalTheme extends Theme {

  private final ServletContext servletContext;
  private final String resourceRoot;
  private final WebappTemplateLoader templateLoader;

  public InternalTheme(String key, Theme parent, ServletContext servletContext, String resourcePath) {
    super(key, parent);
    this.servletContext = Preconditions.checkNotNull(servletContext);
    this.resourceRoot = Preconditions.checkNotNull(resourcePath);
    this.templateLoader = new WebappTemplateLoader(servletContext, resourcePath);
  }

  @Override
  public TemplateLoader getTemplateLoader() throws IOException {
    return templateLoader;
  }

  @Override
  protected InputStream fetchStaticResource(String path) throws IOException {
    return servletContext.getResourceAsStream(resourceRoot + path);
  }

}