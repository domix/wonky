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
package wonky.service;

import io.micronaut.caffeine.cache.Cache;
import io.micronaut.caffeine.cache.Caffeine;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Value;
import io.reactivex.Maybe;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.yaml.snakeyaml.Yaml;
import wonky.api.Invite;
import wonky.http.SlackClient;
import wonky.model.Organization;
import wonky.slack.Team;
import wonky.tracing.TraceUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

/**
 * Created by domix on 01/06/18.
 */
@Context
@Slf4j
public class SlackService {
  @Value("${wonky.tenants.file:/etc/wonky/tenants.yaml}")
  private String tenantsFile;

  @Value("${wonky.tenants.file.pollinterval:100}")
  private int POLL_INTERVAL = 100;

  private List<SlackOrganization> orgs;

  @Inject
  private TraceUtil traceUtil;

  @Inject
  private SlackClient slackClient;

  private Cache<String, Organization> cache;

  @PostConstruct
  public void init() {
    log.info("Tenant file {}", tenantsFile);
    File file = new File(tenantsFile);
    String tenantsFileDirectory = file.getParentFile().getAbsolutePath();
    log.info("Watching changes in [{}]", tenantsFileDirectory);
    log.info("Poll interval [{}]", POLL_INTERVAL);
    FileAlterationObserver observer = new FileAlterationObserver(tenantsFileDirectory);
    FileAlterationMonitor monitor = new FileAlterationMonitor(POLL_INTERVAL);

    load();

    FileAlterationListener listener = new FileAlterationListenerAdaptor() {
      @Override
      public void onFileChange(File file) {
        log.debug("onFileChange");
        log.info("Reloading file [{}]", file.getAbsoluteFile().getName());
        //TODO: verify that the file changed is the configured file
        load();
      }
    };
    observer.addListener(listener);
    monitor.addObserver(observer);
    try {
      monitor.start();
    } catch (Exception e) {
      throw new IllegalStateException(e.getMessage(), e);
    }

    cache = Caffeine.newBuilder()
      .expireAfterWrite(10, TimeUnit.MINUTES)
      .maximumSize(100)
      .build();

    orgs.forEach(slackOrganization -> this.get(slackOrganization.getWonkyDomain()));
  }

  public void load() {
    log.info("Loading [{}]", tenantsFile);
    Yaml yaml = new Yaml();

    try {
      File file = new File(tenantsFile);
      InputStream ios = new FileInputStream(file);
      orgs = yaml.load(ios);

      ios.close();
    } catch (FileNotFoundException e) {
      log.error(format("Can't load tenants file. '%s'", tenantsFile), e);
      throw new IllegalStateException(format("Can't load tenants file. '%s'", tenantsFile), e);
    } catch (IOException e) {
      log.warn(e.getMessage(), e);
    }
  }

  public List<SlackOrganization> getOrgs() {
    return orgs;
  }

  public Maybe<Organization> get(String hostname) {
    log.warn("Getting {}", hostname);

    return Optional.ofNullable(cache.getIfPresent(hostname))
      .map(organization -> {
        log.info("Cached Data");
        return Maybe.just(organization);
      })
      .orElseGet(() -> traceUtil.trace(span ->
        findTenant(hostname)
          .map(slackOrganization -> tenantSlackInformation(slackOrganization.getToken())
            .map(team -> {
              Organization organization = new Organization();
              organization.setTeam(team);
              log.info("Saving in cache...");
              cache.put(hostname, organization);
              return organization;
            }))
          .orElseThrow(() -> throwSlackOrganizationNotFoundException(hostname))));
  }

  public Optional<SlackOrganization> findTenant(String hostname) {
    return traceUtil.trace(span -> orgs.stream()
      .filter(slackOrganization -> slackOrganization.getWonkyDomain().equals(hostname))
      .findFirst());
  }

  public Maybe<Team> tenantSlackInformation(String token) {
    return slackClient.fetchTeamInfo(token).firstElement();
  }

  public void setTenantsFile(String tenantsFile) {
    this.tenantsFile = tenantsFile;
  }

  public Maybe<String> invite(String hostname, Invite invite) {
    SlackOrganization tenant = findTenant(hostname)
      .orElseThrow(() -> throwSlackOrganizationNotFoundException(hostname));

    return slackClient.invite(tenant, invite.getEmail())
      .firstElement();
  }

  private EntityNotFoundException throwSlackOrganizationNotFoundException(String hostname) {
    return new EntityNotFoundException("Slack Organization", hostname);
  }
}
