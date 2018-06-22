package wonky.api;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import lombok.extern.slf4j.Slf4j;
import wonky.model.Organization;
import wonky.service.SlackService;

import javax.inject.Inject;

/**
 * Created by domix on 01/06/18.
 */
@Slf4j
@Controller("/v1/organizations")
public class ApiController {
  @Inject
  private SlackService slackService;

  @Get("/_self")
  public Organization index(@Header("Host") String hostname) {
    log.info("Looking for [{}]", hostname);
    return slackService.get(hostname);
  }
}
