package wonky.tracing;

import io.micronaut.tracing.interceptor.TraceInterceptor;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;

@Singleton
@Slf4j
public class TraceUtil {
  private final Tracer tracer;

  public TraceUtil(Tracer tracer) {
    this.tracer = tracer;
  }

  public <T> T trace(Supplier<T> supplier) {
    return null;
  }

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
      span.setTag("className", className);
      span.setTag("methodName", methodName);
      //span.setTag("lineNumber", lineNumber);
      span.log(format("%s:%s", operationName, lineNumber));

      try {
        return function.apply(span);
      } catch (RuntimeException e) {
        TraceInterceptor.logError(scope.span(), e);
        throw e;
      }
    }

  }
}
