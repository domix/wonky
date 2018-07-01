package wonky.tracing;

import io.opentracing.Tracer;

import javax.inject.Singleton;

@Singleton
public class TraceUtil {
  private final Tracer tracer;

  public TraceUtil(Tracer tracer) {
    this.tracer = tracer;
  }
}
