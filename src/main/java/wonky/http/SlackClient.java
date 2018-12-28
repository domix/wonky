package wonky.http;

import io.micronaut.core.io.buffer.ByteBuffer;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Flowable;
import wonky.json.JacksonUtil;
import wonky.slack.Team;

import javax.inject.Singleton;
import java.util.Map;

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
    HttpRequest<?> req = HttpRequest.GET(uri);

    return httpClient.exchange(req)
      .map(this::getTeamFromSlackResponse);
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
