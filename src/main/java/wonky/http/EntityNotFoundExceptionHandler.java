package wonky.http;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import wonky.service.EntityNotFoundException;

import javax.inject.Singleton;
import java.util.Map;

import static io.micronaut.core.util.CollectionUtils.mapOf;

@Produces
@Singleton
@Requires(classes = {EntityNotFoundException.class, ExceptionHandler.class})
public class EntityNotFoundExceptionHandler implements ExceptionHandler<EntityNotFoundException, HttpResponse> {

  @Override
  public HttpResponse handle(HttpRequest request, EntityNotFoundException exception) {
    Map data = mapOf(
      "message", exception.getMessage(),
      "entity", exception.getEntity(),
      "id", exception.getId());
    return HttpResponse.notFound(data);
  }
}
