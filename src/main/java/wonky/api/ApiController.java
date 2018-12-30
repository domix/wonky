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
package wonky.api;

import io.micronaut.http.annotation.*;
import io.reactivex.Maybe;
import lombok.extern.slf4j.Slf4j;
import wonky.model.Organization;
import wonky.service.SlackService;
import wonky.tracing.TraceUtil;

import java.util.Optional;

import static java.lang.String.format;

/**
 * Created by domix on 01/06/18.
 */
@Slf4j
@Controller("/v1")
public class ApiController {
  private SlackService slackService;
  private TraceUtil traceUtil;

  public ApiController(SlackService slackService, TraceUtil traceUtil) {
    this.slackService = slackService;
    this.traceUtil = traceUtil;
  }

  @Get("/organizations/_self")
  public Maybe<Organization> index(@Header("Host") String hostname, @Header(value = "Accept-Language", defaultValue = "en") String language) {
    String locale = locale(language);
    return getOrganizationByDomain(hostname);
  }

  private String locale(@Header(value = "Accept-Language", defaultValue = "en") String language) {
    return Optional.ofNullable(language).orElse("en");
  }

  @Get("/organizations/{hostname}")
  public Maybe<Organization> forDomain(@QueryValue("hostname") String hostname, @Header(value = "Accept-Language", defaultValue = "en") String language) {
    return getOrganizationByDomain(hostname);
  }

  @Post("/invites")
  public Maybe<String> invite(@Header("Host") String hostname, @Body Invite invite, @Header(value = "Accept-Language", defaultValue = "en") String language) {
    return slackService.invite(hostname, invite);
  }

  private Maybe<Organization> getOrganizationByDomain(String hostname) {
    log.info("Looking for [{}]", hostname);

    return traceUtil.trace(span -> {
      span.log(format("Looking for [%s]", hostname));
      return slackService.get(hostname);
    });
  }
}
