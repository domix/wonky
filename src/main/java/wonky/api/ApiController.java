package wonky.api;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import wonky.service.SlackOrganization;
import wonky.service.SlackService;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by domix on 01/06/18.
 */
@Controller("/hello")
public class ApiController {
  @Inject
  private SlackService slackService;

  @Get("/")
  public List<SlackOrganization> index() {
    return slackService.getOrgs();
  }
}
