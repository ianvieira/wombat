package org.ambraproject.wombat.controller;

import com.google.common.collect.ImmutableMap;
import org.ambraproject.wombat.service.ArticleTransformService;
import org.ambraproject.wombat.service.EntityNotFoundException;
import org.ambraproject.wombat.service.SoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class FigurePageController extends WombatController {

  @Autowired
  private SoaService soaService;
  @Autowired
  private ArticleTransformService articleTransformService;

  /**
   * Apply a site's article transformation to a figure's {@code description} member and store the result in a new {@code
   * descriptionHtml} member.
   *
   * @param site           the key of the site whose article transformation should be applied
   * @param figureMetadata the figure metadata object (per the service API's JSON response) to be read and added to
   */
  private void transformFigureDescription(String site, Map<String, Object> figureMetadata) {
    String description = (String) figureMetadata.get("description");
    String descriptionHtml;
    try {
      descriptionHtml = articleTransformService.transformExcerpt(site, description, "desc");
    } catch (TransformerException e) {
      throw new RuntimeException(e);
    }
    descriptionHtml = kludgeRelativeImageLinks(descriptionHtml);
    figureMetadata.put("descriptionHtml", descriptionHtml);
  }

  /**
   * The transform is written assuming we're at the article path, but because we're also (probably improperly) reusing
   * it here, the paths are wrong. Unlike in FreeMarker, there's no apparent, easy way to configure what the path should
   * be on a per-transformation basis. So kludge in the fix after the fact.
   * <p/>
   * TODO something less horrible
   */
  private static String kludgeRelativeImageLinks(String descriptionHtml) {
    return descriptionHtml.replace("<img src=\"article/", "<img src=\"../article/");
  }

  /**
   * Serve a page listing all figures for an article.
   */
  @RequestMapping("/{site}/article/figures")
  public String renderFiguresPage(Model model,
                                  @PathVariable("site") String site,
                                  @RequestParam("doi") String articleId)
      throws IOException {
    requireNonemptyParameter(articleId);
    Map<String, Object> articleMetadata;
    try {
      articleMetadata = soaService.requestObject("articles/" + articleId, Map.class);
    } catch (EntityNotFoundException enfe) {
      throw new ArticleNotFoundException(articleId);
    }
    model.addAttribute("article", articleMetadata);

    List<Map<String, Object>> figureMetadataList = (List<Map<String, Object>>) articleMetadata.get("figures");
    for (Map<String, Object> figureMetadata : figureMetadataList) {
      transformFigureDescription(site, figureMetadata);
    }

    return site + "/ftl/article/figures";
  }

  /**
   * Serve a page displaying a single figure.
   */
  @RequestMapping("/{site}/article/figure")
  public String renderFigurePage(Model model,
                                 @PathVariable("site") String site,
                                 @RequestParam("id") String figureId)
      throws IOException {
    requireNonemptyParameter(figureId);
    Map<String, Object> figureMetadata;
    try {
      figureMetadata = soaService.requestObject("assets/" + figureId + "?figure", Map.class);
    } catch (EntityNotFoundException enfe) {
      throw new ArticleNotFoundException(figureId);
    }
    transformFigureDescription(site, figureMetadata);
    model.addAttribute("figure", figureMetadata);

    String parentArticleId = (String) figureMetadata.get("parentArticleId");
    model.addAttribute("article", ImmutableMap.of("doi", parentArticleId));

    return site + "/ftl/article/figure";
  }

}
