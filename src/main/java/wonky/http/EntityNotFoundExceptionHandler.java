package wonky.http;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import wonky.service.EntityNotFoundException;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Produces
@Singleton
@Requires(classes = {EntityNotFoundException.class, ExceptionHandler.class})
public class EntityNotFoundExceptionHandler implements ExceptionHandler<EntityNotFoundException, HttpResponse> {

  @Override
  public HttpResponse handle(HttpRequest request, EntityNotFoundException exception) {
    Map data = new HashMap();
    data.put("message", exception.getMessage());
    data.put("entity", exception.getEntity());
    data.put("id", exception.getId());
    return HttpResponse.notFound(data);
  }
}
