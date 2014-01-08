/*
 * $HeadURL$
 * $Id$
 * Copyright (c) 2006-2013 by Public Library of Science http://plos.org http://ambraproject.org
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ambraproject.wombat.controller;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import org.ambraproject.wombat.config.Site;
import org.ambraproject.wombat.config.SiteSet;
import org.ambraproject.wombat.service.UnmatchedSiteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.ListIterator;

/**
 * Base class with common functionality for all controllers in the application.
 */
public abstract class WombatController {

  private static final Logger log = LoggerFactory.getLogger(WombatController.class);

  @Autowired
  protected SiteSet siteSet;

  /**
   * Handler invoked for all uncaught exceptions.  Renders a "nice" 500 page.
   *
   * @param e        uncaught exception
   * @param request  HttpServletRequest
   * @param response HttpServletResponse
   * @return ModelAndView specifying the view
   * @throws IOException
   */
  @ExceptionHandler(Exception.class)
  protected ModelAndView handleException(Exception e, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    log.error("handleException", e);
    response.setStatus(500);
    SitePageContext context = inspectPathForContext(request);

    // For some reason, methods decorated with @ExceptionHandler cannot accept Model parameters,
    // unlike @RequestMapping methods.  So this is a little different...
    ModelAndView mav = new ModelAndView(context.getSite().getKey() + "/ftl/error");
    mav.addObject("depth", context.getPageDepth());
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    e.printStackTrace(new PrintStream(baos));

    // No need to close stream since it's a ByteArrayOutputStream.

    mav.addObject("stackTrace", baos.toString("utf-8"));
    return mav;
  }

  /**
   * Directs unhandled ArticleNotFoundExceptions to a 404 page.
   *
   * @param request  HttpServletRequest
   * @param response HttpServletResponse
   * @return ModelAndView specifying the view
   */
  @ExceptionHandler({MissingServletRequestParameterException.class, ArticleNotFoundException.class})
  protected ModelAndView handleArticleNotFound(HttpServletRequest request, HttpServletResponse response) {
    response.setStatus(404);
    SitePageContext context = inspectPathForContext(request);

    // TODO: do we want an "article not found" page separate from the generic 404 page?
    ModelAndView mav = new ModelAndView(context.getSite().getKey() + "/ftl/notFound");
    mav.addObject("depth", context.getPageDepth());
    return mav;
  }

  /**
   * The inferred location of a page from inspecting the path.
   */
  protected static class SitePageContext {
    private final Site site;
    private final int pageDepth;

    private SitePageContext(Site site, int pageDepth) {
      this.site = Preconditions.checkNotNull(site);
      this.pageDepth = pageDepth;
      Preconditions.checkArgument(this.pageDepth >= 0);
    }

    /**
     * @return the site to which the page belongs
     */
    public Site getSite() {
      return site;
    }

    /**
     * @return the number of steps from the page's path to the site root
     */
    public int getPageDepth() {
      return pageDepth;
    }
  }

  private static final Splitter PATH_SPLITTER = Splitter.on('/');

  /**
   * Attempts to extract the site from the request. Note that controllers should usually get the site using a
   * &at;PathVariable("site") annotation on a @RequestMapping method; this method is provided for the rare cases when
   * this is not possible.
   *
   * @param request HttpServletRequest
   * @return the site and page depth, or null if no site key was found in the request path
   */
  protected SitePageContext inspectPathForContext(HttpServletRequest request) {
    // Must use Splitter instead of String.split, because String.split ignores the final slash
    List<String> pathComponents = PATH_SPLITTER.splitToList(request.getServletPath());

    // Iterate over parts of the path, looking for the first that is a site name
    for (ListIterator<String> iter = pathComponents.listIterator(); iter.hasNext(); ) {
      String possibleSite = iter.next();

      Site site;
      try {
        site = siteSet.getSite(possibleSite);
      } catch (UnmatchedSiteException use) {
        continue; // try the next path component
      }

      int pageDepth = pathComponents.size() - iter.previousIndex() - 2;
      return new SitePageContext(site, pageDepth);
    }
    return null; // no site name matched
  }

}
