/**
 *
 * Copyright (C) 2014-2019 the original author or authors.
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
