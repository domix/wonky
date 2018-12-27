package wonky.http;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Produces
@Singleton
@Requires(classes = {SlackResponseException.class, ExceptionHandler.class})
public class SlackResponseExceptionHandler implements ExceptionHandler<SlackResponseException, HttpResponse> {

  @Override
  public HttpResponse handle(HttpRequest request, SlackResponseException exception) {
    Map data = new HashMap();
    data.put("message", exception.getMessage());
    data.put("error", exception.getError());
    data.put("slackResponse", exception.getSlackResponse());
    return HttpResponse.serverError(data);
  }
}
