/*
 * Copyright (c) 2017 Public Library of Science
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package org.ambraproject.wombat.service.remote;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import org.ambraproject.wombat.service.EntityNotFoundException;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.xerces.impl.dv.util.Base64;
import org.plos.ned_client.model.IndividualComposite;
import org.plos.ned_client.model.Individualprofile;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

public class UserApiImpl extends AbstractRestfulJsonApi implements UserApi {

  @Autowired
  private ArticleApi articleApi;

  // Configuration data for sending requests to the remote user service.
  // Lazily initialized. We can't create it until articleApi is wired.
  private static class UserApiConfiguration {
    private final URL server;
    private final ImmutableCollection<BasicHeader> authorizationHeader;

    private UserApiConfiguration(String server, String authorizationAppName, String authorizationPassword) {
      try {
        this.server = new URL(Objects.requireNonNull(server));
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }

      this.authorizationHeader = ((authorizationAppName != null) && (authorizationPassword != null))
          ? ImmutableList.of(createAuthorizationHeader(authorizationAppName, authorizationPassword))
          : ImmutableList.of();
    }
  }

  // There is probably a library for this. TODO
  private static BasicHeader createAuthorizationHeader(String authorizationAppName, String authorizationPassword) {
    String authorization = authorizationAppName + ":" + authorizationPassword;
    String encoded = Base64.encode(authorization.getBytes(Charsets.ISO_8859_1));
    return new BasicHeader("Authorization", "Basic " + encoded);
  }

  private static final ApiAddress USER_API_CONFIG = ApiAddress.builder("config/userApi").build();

  private UserApiConfiguration fetchApiConfiguration() {
    Map<String, ?> userConfigData;
    try {
      userConfigData = articleApi.requestObject(USER_API_CONFIG, Map.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    String server = (String) userConfigData.get("server");
    if (server == null) {
      throw new RuntimeException("userApi is not configured");
    }
    return new UserApiConfiguration(server,
        (String) userConfigData.get("authorizationAppName"),
        (String) userConfigData.get("authorizationPassword"));
  }

  private transient UserApiConfiguration userApiConfiguration;

  private UserApiConfiguration getUserApiConfiguration() {
    return (userApiConfiguration != null) ? userApiConfiguration : (userApiConfiguration = fetchApiConfiguration());
  }

  @Override
  protected Iterable<? extends Header> getAdditionalHeaders() {
    return getUserApiConfiguration().authorizationHeader;
  }

  @Override
  protected URL getServerUrl() {
    return getUserApiConfiguration().server;
  }

  @Override
  protected String getCachePrefix() {
    return "user";
  }

  @Override
  protected <T> T makeRemoteRequest(RemoteRequest<T> requestAction) throws IOException {
    try {
      return super.makeRemoteRequest(requestAction);
    } catch (ServiceRequestException | ServiceConnectionException | ServiceResponseFormatException |
        EntityNotFoundException e) {
      throw new UserApiException(e);
    }
  }

  @Override
  public final String getUserIdFromAuthId(String authId) throws IOException {
    Objects.requireNonNull(authId);
    final IndividualComposite individualComposite;
    individualComposite = requestObject(ApiAddress.builder("individuals").addToken("CAS")
        .addToken(authId).build(), IndividualComposite.class);
    Individualprofile individualprofile = individualComposite.getIndividualprofiles().stream()
        .findFirst() // Multiple hits may be possible. Using the first is recommended for this API.
        .orElseThrow(() -> new UserApiException(
            "An IndividualComposite does not have an Individualprofile"));
    return individualprofile.getNedid().toString();
  }

}
