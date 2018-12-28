package wonky.http;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import javax.inject.Singleton;
import java.util.Map;

import static io.micronaut.core.util.CollectionUtils.mapOf;

@Produces
@Singleton
@Requires(classes = {SlackResponseException.class, ExceptionHandler.class})
public class SlackResponseExceptionHandler implements ExceptionHandler<SlackResponseException, HttpResponse> {

  @Override
  public HttpResponse handle(HttpRequest request, SlackResponseException exception) {
    Map data = mapOf(
      "message", exception.getMessage(),
      "error", exception.getError(),
      "slackResponse", exception.getSlackResponse());

    return HttpResponse.serverError(data);
  }
}
