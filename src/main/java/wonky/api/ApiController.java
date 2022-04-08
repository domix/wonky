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
package wonky.api;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpResponseFactory;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import lombok.extern.slf4j.Slf4j;
import wonky.model.Organization;
import wonky.service.SlackService;

import java.util.Optional;

import static io.micronaut.http.HttpHeaders.ACCEPT_LANGUAGE;
import static io.micronaut.http.HttpHeaders.HOST;

/**
 * Created by domix on 01/06/18.
 */
@Slf4j
@Controller("/v1")
@Secured(SecurityRule.IS_ANONYMOUS)
public class ApiController {
  private SlackService slackService;

  public ApiController(SlackService slackService) {
    this.slackService = slackService;
  }

  @Get("/organizations/_self")
  public HttpResponse<Organization> index(@Header(HOST) String hostname, @Header(value = ACCEPT_LANGUAGE, defaultValue = "en") String language) {
    String locale = locale(language);

    return HttpResponseFactory.INSTANCE.ok(getOrganizationByDomain(hostname));
  }

  private String locale(@Header(value = ACCEPT_LANGUAGE, defaultValue = "en") String language) {
    return Optional.ofNullable(language).orElse("en");
  }

  @Get("/organizations/{hostname}")
  public Organization forDomain(@QueryValue("hostname") String hostname, @Header(value = ACCEPT_LANGUAGE, defaultValue = "en") String language) {
    return getOrganizationByDomain(hostname);
  }

  @Post("/invites")
  public String invite(@Header(HOST) String hostname, @Body Invite invite, @Header(value = ACCEPT_LANGUAGE, defaultValue = "en") String language) {
    return slackService.invite(hostname, invite);
  }

  private Organization getOrganizationByDomain(String hostname) {
    log.info("Looking for [{}]", hostname);

    return slackService.get(hostname);
  }
}
