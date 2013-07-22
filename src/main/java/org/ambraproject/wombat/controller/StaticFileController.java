package org.ambraproject.wombat.controller;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Closer;
import org.ambraproject.wombat.config.Theme;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Controller
public class StaticFileController {

  @Autowired
  private ImmutableMap<String, Theme> themesForJournals;

  @RequestMapping("/{journal}/static/**")
  public void serveStaticContent(HttpServletRequest request, HttpServletResponse response,
                                 @PathVariable("journal") String journal)
      throws IOException {
    Theme theme = themesForJournals.get(journal);

    // Kludge to get "static/**"
    String servletPath = request.getServletPath();
    String filePath = servletPath.substring(journal.length() + 2);

    Closer closer = Closer.create();
    try {
      InputStream inputStream = theme.getStaticResource(filePath);
      if (inputStream == null) {
        // TODO: Forward to user-friendly 404 page
        response.setStatus(HttpStatus.NOT_FOUND.value());

        // Just for debugging
        closer.register(response.getOutputStream()).write("Not found!".getBytes());
      } else {
        closer.register(inputStream);
        OutputStream outputStream = closer.register(response.getOutputStream());
        IOUtils.copy(inputStream, outputStream);
      }
    } catch (Throwable t) {
      throw closer.rethrow(t);
    } finally {
      closer.close();
    }
  }

}