/**
 *
 * Copyright (C) 2014-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wonky.tracing;

import io.micronaut.tracing.interceptor.TraceInterceptor;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.util.Objects;
import java.util.function.Function;

import static java.lang.String.format;

@Singleton
@Slf4j
public class TraceUtil {
  private final Tracer tracer;

  public TraceUtil(Tracer tracer) {
    this.tracer = tracer;
  }

  //TODO: implement this
  /*public <T> T trace(Supplier<T> supplier) {
    return null;
  }*/

  public <T> T trace(Function<Span, T> function) {
    StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

    int index = 1;
    if (stackTraceElements.length > 2) {
      index = 2;
    }
    StackTraceElement stackTraceElement = stackTraceElements[index];
    String className = stackTraceElement.getClassName();
    String methodName = stackTraceElement.getMethodName();
    String lineNumber = String.valueOf(stackTraceElement.getLineNumber());

    Span activeSpan = tracer.activeSpan();
    String operationName = format("%s.%s", className, methodName);

    Tracer.SpanBuilder builder = tracer.buildSpan(operationName);

    if (!Objects.isNull(activeSpan)) {
      builder.asChildOf(activeSpan);
    }

    try (Scope scope = builder.startActive(true)) {
      Span span = scope.span();
      span.
        setTag("className", className).
        setTag("methodName", methodName).
        log(format("%s:%s", operationName, lineNumber));

      try {
        return function.apply(span);
      } catch (RuntimeException e) {
        TraceInterceptor.logError(scope.span(), e);
        throw e;
      }
    }

  }
}
