package wonky.http;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.security.NoSuchAlgorithmException;

/**
 * Created by domix on 05/06/18.
 */
public class Client {
  private static SSLEngine defaultSSLEngineForClient(String host, Integer port) {
    SSLContext sslCtx;
    try {
      sslCtx = SSLContext.getDefault();
    } catch (NoSuchAlgorithmException e) {
      //TODO: improve exception handling
      throw new IllegalStateException(e.getMessage(), e);
    }
    SSLEngine sslEngine = sslCtx.createSSLEngine(host, port);
    sslEngine.setUseClientMode(true);
    return sslEngine;
  }

  public static HttpClient<ByteBuf, ByteBuf> secure(String host) {
    int port = 443;
    return HttpClient.newClient(host, port)
      .secure(defaultSSLEngineForClient(host, port));
  }
}
