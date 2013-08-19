package org.ambraproject.wombat.controller;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.io.Closer;
import org.ambraproject.wombat.service.ArticleNotFoundException;
import org.ambraproject.wombat.service.EntityNotFoundException;
import org.ambraproject.wombat.service.SoaService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@Controller
public class FigurePageController {

  @Autowired
  private SoaService soaService;

  /*
   * Dereference keys given in "article.figures" to find assets from "article.assets".
   * Set them up in a new object that can be accessed more easily from FreeMarker.
   *
   * TODO: Do this entirely in FreeMarker?
   */
  private static List<?> buildFigureList(Map<?, ?> articleMetadata) {
    Map<?, ?> assets = (Map<?, ?>) articleMetadata.get("assets");
    List<Map<?, ?>> figureMetadataList = (List<Map<?, ?>>) articleMetadata.get("figures");

    List<Object> figures = Lists.newArrayListWithCapacity(figureMetadataList.size());
    for (Map<?, ?> figureMetadata : figureMetadataList) {
      Map<?, ?> asset = (Map<?, ?>) assets.get(figureMetadata.get("id"));
      Object originalAsset = asset.get(figureMetadata.get("original"));

      List<String> thumbnailAssetIds = (List<String>) figureMetadata.get("thumbnails");
      List<Object> thumbnailAssets = Lists.newArrayListWithCapacity(thumbnailAssetIds.size());
      for (String thumbnailAssetId : thumbnailAssetIds) {
        Object thumbnailAsset = asset.get(thumbnailAssetId);
        thumbnailAssets.add(thumbnailAsset);
      }

      Object figure = ImmutableMap.of("original", originalAsset, "thumbnails", thumbnailAssets);
      figures.add(figure);
    }

    return figures;
  }

  @RequestMapping("/{journal}/article/figures")
  public String renderFigurePage(Model model,
                                 @PathVariable("journal") String journal,
                                 @RequestParam("doi") String articleId)
      throws IOException {
    Map<?, ?> articleMetadata;
    try {
      articleMetadata = soaService.requestObject("articles/" + articleId, Map.class);
    } catch (EntityNotFoundException enfe) {
      throw new ArticleNotFoundException(articleId);
    }
    model.addAttribute("article", articleMetadata);
    model.addAttribute("figures", buildFigureList(articleMetadata));

    return journal + "/ftl/article/figures";
  }

  /**
   * Serve an asset file as the response body. Forward a stream from the SOA.
   */
  @RequestMapping("/{journal}/article/asset")
  public void serveAsset(HttpServletResponse response,
                         @PathVariable("journal") String journal,
                         @RequestParam("id") String assetId)
      throws IOException {
    // TODO: Set response headers

    Closer closer = Closer.create();
    try {
      InputStream assetStream = soaService.requestStream("assetfiles/" + assetId);
      if (assetStream == null) {
        throw new EntityNotFoundException(assetId);
      }
      closer.register(assetStream);
      OutputStream responseStream = closer.register(response.getOutputStream());
      IOUtils.copy(assetStream, responseStream); // buffered
    } catch (Throwable t) {
      throw closer.rethrow(t);
    } finally {
      closer.close();
    }
  }

}
