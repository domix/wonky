/**
 *
 * Copyright (C) 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wonky.http;

import io.micronaut.core.io.buffer.ByteBuffer;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Flowable;
import wonky.json.JacksonUtil;
import wonky.service.SlackOrganization;
import wonky.slack.Team;

import javax.inject.Singleton;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import static io.micronaut.http.HttpRequest.GET;
import static io.micronaut.http.HttpRequest.POST;
import static java.lang.String.format;

@Singleton
public class SlackClient {

  private final RxHttpClient httpClient;
  private final JacksonUtil jacksonUtil;

  public SlackClient(@Client("https://slack.com") RxHttpClient httpClient, JacksonUtil jacksonUtil) {
    this.httpClient = httpClient;
    this.jacksonUtil = jacksonUtil;
  }

  public Flowable<Team> fetchTeamInfo(String token) {
    String uri = format("/api/team.info?token=%s", token);
    HttpRequest<?> req = GET(uri);

    return httpClient.exchange(req)
      .map(this::getTeamFromSlackResponse);
  }

  public Flowable<String> invite(SlackOrganization tenant, String email) {

    String uri = format("/api/users.admin.invite?token=%s", tenant.getToken());
    String encodedEmail;

    try {
      encodedEmail = URLEncoder.encode(email, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("chaz");
    }

    String payload = String.format("email=%s", encodedEmail);

    HttpRequest<?> req = POST(uri, payload)
      .header("Content-Type", "application/x-www-form-urlencoded");

    return httpClient.exchange(req)
      .map(response -> new String(response.getBody().get().toByteArray()));
  }

  private Team getTeamFromSlackResponse(HttpResponse<ByteBuffer> response) {
    validateSlackError(response);
    String body = new String(response.getBody().get().toByteArray());
    return jacksonUtil.readValue(body, "team", Team.class);
  }

  private void validateSlackError(HttpResponse<ByteBuffer> response) {
    String body = new String(response.getBody().get().toByteArray());
    Map map = jacksonUtil.readValue(body, Map.class);
    Object ok = map.get("ok");

    boolean result = Boolean.valueOf(ok.toString());
    if (!result) {
      String error = map.getOrDefault("error", "Unknow").toString();
      throw new SlackResponseException(error, body);
    }
  }
}
