package wonky.http;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Flowable;
import wonky.json.JacksonUtil;
import wonky.slack.Team;

import javax.inject.Singleton;

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

    return httpClient.retrieve(req)
      .map(o -> jacksonUtil.readValue(o, "team", Team.class));
  }
}
