package wonky.security;

import io.micronaut.security.authentication.*;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import javax.inject.Singleton;
import java.util.ArrayList;

@Singleton
public class AuthenticationProviderUserPassword implements AuthenticationProvider {
  @Override
  public Publisher<AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest) {
    if (authenticationRequest.getIdentity().equals("user") && authenticationRequest.getSecret().equals("password")) {
      return Flowable.just(new UserDetails("user", new ArrayList<>()));
    }
    return Flowable.just(new AuthenticationFailed());
  }
}
