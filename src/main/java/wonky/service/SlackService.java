package wonky.service;

import io.micronaut.context.annotation.Value;
import io.reactivex.Maybe;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.yaml.snakeyaml.Yaml;
import wonky.http.SlackClient;
import wonky.json.JacksonUtil;
import wonky.model.Organization;
import wonky.slack.Team;
import wonky.tracing.TraceUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Created by domix on 01/06/18.
 */
@Singleton
@Slf4j
public class SlackService {
  private static final String slack = "slack.com";
  @Value("${wonky.tenants.file:/etc/wonky/tenants.yaml}")
  private String tenantsFile;

  @Value("${wonky.tenants.file.pollinterval:100}")
  private int POLL_INTERVAL = 100;

  private List<SlackOrganization> orgs;

  @Inject
  private JacksonUtil jacksonUtil;

  @Inject
  private TraceUtil traceUtil;

  @Inject
  private SlackClient slackClient;

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

  //TODO: make this data cacheable
  public Maybe<Organization> get(String hostname) {
    log.warn("Getting {}", hostname);
    return traceUtil.trace(span ->
      findTenant(hostname)
        .map(slackOrganization -> tenantSlackInformation(slackOrganization.getToken())
          .map(team -> {
            Organization organization = new Organization();
            organization.setTeam(team);
            return organization;
          })).orElseThrow(() -> new EntityNotFoundException("Slack Organization", hostname)));
  }

  public Optional<SlackOrganization> findTenant(String hostname) {
    return traceUtil.trace(span -> orgs.stream()
      .filter(slackOrganization -> slackOrganization.getWonkyDomain().equals(hostname))
      .peek(slackOrganization -> {
        System.out.println("Domain: " + slackOrganization.getWonkyDomain());
      })
      .findFirst());
  }

  public Maybe<Team> tenantSlackInformation(String token) {
    return slackClient.fetchTeamInfo(token).firstElement();
  }


  public void setTenantsFile(String tenantsFile) {
    this.tenantsFile = tenantsFile;
  }

  public void invite(String hostname, String email) {
    /*SlackOrganization tenant = findTenant(hostname).orElse(null);
    String uri = format("/api/users.admin.invite?token=%s", tenant.getToken());
    String encodedEmail = new String(java.util.Base64.getEncoder().encode(email.getBytes()));

    String payload = String.format("email=%s", encodedEmail);

    Client.secure(slack).createPost(uri)
      .setHeader("Content-Type", "application/x-www-form-urlencoded")
      .writeStringContent(Observable.just(payload));*/
  }
  /*
  Map invite(String hostname, String email) {
    findTenant(hostname)
      .map { invite(it.token, it.teamDomain, email) }
      .orElse(emptyMap())
  }

  Map invite(String token, String host, String email) {
    Map response = remoteService.post("https://${host}.slack.com/api/users.admin.invite") {
      charset 'UTF-8'
      urlenc token: token, email: email
    }

    response + ofNullable(response.ok).filter { it == true }.map {
      [message: messageSource.get('invite.success')]
    }.orElseGet {
      [error: messageSource.get(response.error)]
    }
  }
   */
}
