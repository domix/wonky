package wonky.tracing;

import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Slf4j
public class TraceUtil {
  private final Tracer tracer;

  @Inject
  public TraceUtil(Tracer tracer) {
    this.tracer = tracer;
  }

  public <T> T trace(String content, Class<T> valueType) {
    Span currentSpan = tracer.activeSpan();
    Tracer.SpanBuilder dd = tracer.buildSpan("dd");
    Span start = dd.withTag("hostname", "localhost").start();
    log.info("Looking for [{}]", "localhost");

    start.log("Calling done.");
    start.finish();
    return null;
  }
}
