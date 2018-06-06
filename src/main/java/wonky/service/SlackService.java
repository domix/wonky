package wonky.service;

import io.micronaut.context.annotation.Value;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.yaml.snakeyaml.Yaml;
import rx.Observable;
import wonky.http.Client;
import wonky.json.JacksonUtil;
import wonky.model.Organization;
import wonky.slack.Team;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.nio.charset.Charset.defaultCharset;

/**
 * Created by domix on 01/06/18.
 */
@Singleton
public class SlackService {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SlackService.class);
  private static final String slack = "slack.com";
  @Value("${wonky.tenants.file:/etc/wonky/tenants.yaml}")
  private String tenantsFile;

  @Value("${wonky.tenants.file.pollinterval:100}")
  private int POLL_INTERVAL = 100;

  private List<SlackOrganization> orgs;

  @Inject
  private JacksonUtil jacksonUtil;

  public void setJacksonUtil(JacksonUtil jacksonUtil) {
    this.jacksonUtil = jacksonUtil;
  }

  @PostConstruct
  public void init() {
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
        //TODO: verify that the file chaged is the configured file
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
  public Organization get(String hostname) {
    return findTenant(hostname).map(slackOrganization -> {
      Team team = tenantSlackInformation(slackOrganization.getToken());
      Organization organization = new Organization();
      organization.setTeam(team);
      return organization;
    }).orElse(null);
  }

  public void setOrgs(List<SlackOrganization> orgs) {
    this.orgs = orgs;
  }

  public Optional<SlackOrganization> findTenant(String hostname) {
    return orgs.stream()
      .filter(slackOrganization -> slackOrganization.getWonkyDomain().equals(hostname))
      .findFirst();
  }

  public Team tenantSlackInformation(String token) {

    String uri = format("/api/team.info?token=%s", token);

    return Client.secure(slack)
      .createGet(uri)
      .flatMap(resp ->
        resp.getContent()
          .map(bb ->
            jacksonUtil.readValue(bb.toString(defaultCharset()), "team", Team.class)))
      .toBlocking().firstOrDefault(null);
  }


  public void setTenantsFile(String tenantsFile) {
    this.tenantsFile = tenantsFile;
  }

  public void invite(String hostname, String email) {
    SlackOrganization tenant = findTenant(hostname).orElse(null);
    String uri = format("/api/users.admin.invite?token=%s", tenant.getToken());
    String encodedEmail = new String(Base64.getEncoder().encode(email.getBytes()));

    String payload = String.format("email=%s", encodedEmail);

    Client.secure(slack).createPost(uri)
      .setHeader("Content-Type", "application/x-www-form-urlencoded")
      .writeStringContent(Observable.just(payload));
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
