/**
 * Copyright (C) 2014-2019 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wonky.http;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Singleton;
import wonky.json.JacksonUtil;
import wonky.service.SlackOrganization;
import wonky.slack.Team;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import static io.micronaut.http.HttpHeaders.CONTENT_TYPE;
import static io.micronaut.http.HttpRequest.GET;
import static io.micronaut.http.HttpRequest.POST;
import static io.micronaut.http.MediaType.APPLICATION_FORM_URLENCODED;
import static java.lang.String.format;

@Singleton
public class SlackClient {

  private final HttpClient httpClient;
  private final JacksonUtil jacksonUtil;

  public SlackClient(@Client("https://slack.com") HttpClient httpClient, JacksonUtil jacksonUtil) {
    this.httpClient = httpClient;
    this.jacksonUtil = jacksonUtil;
  }

  public Team fetchTeamInfo(String token) {
    String uri = format("/api/team.info?token=%s", token);
    HttpRequest<?> req = GET(uri);

    String retrieve = httpClient.toBlocking().retrieve(req);

    return getTeamFromSlackResponse(retrieve);
  }

  public String invite(SlackOrganization tenant, String email) {

    String uri = format("/api/users.admin.invite?token=%s", tenant.getToken());
    String encodedEmail;

    try {
      encodedEmail = URLEncoder.encode(email, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("chaz");
    }

    String payload = String.format("email=%s", encodedEmail);

    HttpRequest<?> req = POST(uri, payload)
      .header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED);

    return httpClient.toBlocking().exchange(req, String.class).body();
  }

  private Team getTeamFromSlackResponse(String response) {
    validateSlackError(response);
    return jacksonUtil.readValue(response, "team", Team.class);
  }

  private void validateSlackError(String body) {
    final var map = jacksonUtil.readValue(body, Map.class);
    Object ok = map.get("ok");

    boolean result = Boolean.parseBoolean(ok.toString());
    if (!result) {
      String error = map.getOrDefault("error", "Unknown").toString();
      throw new SlackResponseException(error, body);
    }
  }
}
