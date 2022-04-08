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
package wonky.service;

import io.micronaut.caffeine.cache.Cache;
import io.micronaut.caffeine.cache.Caffeine;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.yaml.snakeyaml.Yaml;
import wonky.api.Invite;
import wonky.http.SlackClient;
import wonky.http.SlackResponseException;
import wonky.model.Organization;
import wonky.slack.Team;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
  private final SlackClient slackClient;
  private final String tenantsFile;
  private final int pollInterval;
  private List<SlackOrganization> orgs;
  private Cache<String, Organization> cache;

  public SlackService(
    SlackClient slackClient,
    @Value("${wonky.tenants.file:/etc/wonky/tenants.yaml}") final String tenantsFile,
    @Value("${wonky.tenants.file.pollinterval:100}") final int pollInterval
  ) {
    this.slackClient = slackClient;
    this.tenantsFile = tenantsFile;
    this.pollInterval = pollInterval;
  }

  @PostConstruct
  public void init() {
    log.info("Tenant file {}", tenantsFile);
    File file = new File(tenantsFile);
    String tenantsFileDirectory = file.getParentFile().getAbsolutePath();
    log.info("Watching changes in [{}]", tenantsFileDirectory);
    log.info("Poll interval [{}]", pollInterval);
    FileAlterationObserver observer = new FileAlterationObserver(tenantsFileDirectory);
    FileAlterationMonitor monitor = new FileAlterationMonitor(pollInterval);

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
      .expireAfterWrite(24, TimeUnit.HOURS)
      .maximumSize(100)
      .build();
  }

  public void loadAllOrgs() {
    orgs.parallelStream()
      .forEach(this::handleErrorFromSlack);
  }

  private void handleErrorFromSlack(SlackOrganization slackOrganization) {
    try {
      this.get(slackOrganization.getWonkyDomain());
    } catch (SlackResponseException ex) {
      log.warn(ex.getMessage(), ex);
    }
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

  public Organization get(String hostname) {
    log.warn("Getting {}", hostname);

    return Optional.ofNullable(cache.getIfPresent(hostname))
      .map(organization -> {
        log.info("Cached Data");
        return organization;
      })
      .orElseGet(() -> findTenant(hostname)
        .map(slackOrganization -> {
            Team team = tenantSlackInformation(slackOrganization.getToken());

            Organization organization = new Organization();
            organization.setTeam(team);
            log.info("Saving in cache: {}...", organization.getTeam().getName());
            cache.put(hostname, organization);
            return organization;

          }
        )
        .orElseThrow(() -> throwSlackOrganizationNotFoundException(hostname)));
  }

  public Optional<SlackOrganization> findTenant(String hostname) {
    return
      orgs.stream()
        .filter(slackOrganization -> slackOrganization.getWonkyDomain().equals(hostname))
        .findFirst();
  }

  public Team tenantSlackInformation(String token) {
    return this.slackClient.fetchTeamInfo(token);
  }

  public String invite(String hostname, Invite invite) {
    SlackOrganization tenant = findTenant(hostname)
      .orElseThrow(() -> throwSlackOrganizationNotFoundException(hostname));

    return slackClient.invite(tenant, invite.getEmail());
  }

  private EntityNotFoundException throwSlackOrganizationNotFoundException(String hostname) {
    log.warn("No config found for {}", hostname);
    return new EntityNotFoundException("Slack Organization", hostname);
  }
}
