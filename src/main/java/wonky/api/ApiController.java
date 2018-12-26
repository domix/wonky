package wonky.api;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.reactivex.Maybe;
import lombok.extern.slf4j.Slf4j;
import wonky.model.Organization;
import wonky.service.SlackService;
import wonky.tracing.TraceUtil;

import static java.lang.String.format;

/**
 * Created by domix on 01/06/18.
 */
@Slf4j
@Controller("/v1/organizations")
public class ApiController {
  private SlackService slackService;
  private TraceUtil traceUtil;

  public ApiController(SlackService slackService, TraceUtil traceUtil) {
    this.slackService = slackService;
    this.traceUtil = traceUtil;
  }

  @Get("/_self")
  public Maybe<Organization> index(@Header("Host") String hostname) {
    log.info("Looking for [{}]", hostname);

    return traceUtil.trace(span -> {
      span.log(format("Looking for [%s]", hostname));
      return slackService.get(hostname);
    });
  }
}
