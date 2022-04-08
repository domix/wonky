/**
 * Copyright (C) 2014-2019 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wonky.http;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import wonky.service.EntityNotFoundException;

import static io.micronaut.core.util.CollectionUtils.mapOf;

@Produces
@Singleton
@Requires(classes = {EntityNotFoundException.class, ExceptionHandler.class})
public class EntityNotFoundExceptionHandler implements ExceptionHandler<EntityNotFoundException, HttpResponse<?>> {

  @Override
  public HttpResponse<?> handle(HttpRequest request, EntityNotFoundException exception) {
    final var data = mapOf(
      "message", exception.getMessage(),
      "entity", exception.getEntity(),
      "id", exception.getId());
    return HttpResponse.notFound(data);
  }
}
