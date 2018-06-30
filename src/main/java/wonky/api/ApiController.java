package wonky.api;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.opentracing.Span;
import io.opentracing.Tracer;
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
  @Inject
  private Tracer tracer;

  @Get("/_self")
  public Organization index(@Header("Host") String hostname) {
    Tracer.SpanBuilder dd = tracer.buildSpan("dd");
    Span start = dd.withTag("host.name", hostname).start();
    log.info("Looking for [{}]", hostname);
    Organization organization = slackService.get(hostname);
    start.log("Lalamada termninada");
    start.finish();
    return organization;
  }
}
