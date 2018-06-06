package wonky.api;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import wonky.model.Organization;
import wonky.service.SlackService;

import javax.inject.Inject;

/**
 * Created by domix on 01/06/18.
 */
@Controller("/v1/organizations")
public class ApiController {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ApiController.class);
  @Inject
  private SlackService slackService;

  @Get("/_self")
  public Organization index(@Header("Host") String hostname) {
    log.info("Looking for [{}]", hostname);
    return slackService.get(hostname);
  }
}
