package org.ambraproject.wombat.config.site;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableTable;
import org.springframework.web.bind.annotation.RequestMapping;

public final class HandlerDirectory {

  private final ImmutableTable.Builder<String, Site, String> registry;
  private ImmutableTable<String, Site, String> table;

  public HandlerDirectory() {
    registry = ImmutableTable.builder();
    table = null; // Initialized on first call to getPattern. Treat as final by convention afterward.
  }

  public void register(RequestMapping handlerAnnotation, Site site, String pattern) {
    String handlerName = Preconditions.checkNotNull(handlerAnnotation.name());
    Preconditions.checkNotNull(site);
    Preconditions.checkNotNull(pattern);

    if (table != null) {
      throw new IllegalStateException("Cannot register more methods after directory has been read");
    }
    synchronized (registry) {
      registry.put(handlerName, site, pattern);
    }
  }

  public String getPattern(String handlerName, Site site) {
    if (table == null) {
      table = registry.build();
    }
    return table.get(Preconditions.checkNotNull(handlerName), Preconditions.checkNotNull(site));
  }

}
