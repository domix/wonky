package wonky.service;

import io.micronaut.context.annotation.Value;
import io.netty.handler.logging.LogLevel;
import io.reactivex.netty.protocol.http.client.HttpClient;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

/**
 * Created by domix on 01/06/18.
 */
@Singleton
public class SlackService {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SlackService.class);
  @Value("${wonky.tenants.file:/etc/wonky/tenants.yaml}")
  private String tenantsFile;

  @Value("${wonky.tenants.file.pollinterval:100}")
  private int POLL_INTERVAL = 100;

  private List<SlackOrganization> orgs;


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

  public void setOrgs(List<SlackOrganization> orgs) {
    this.orgs = orgs;
  }

  public SlackOrganization findTenant(String hostname) {
    return orgs.stream()
      .filter(slackOrganization -> slackOrganization.getWonkyDomain().equals(hostname))
      .findFirst().orElseThrow(() -> new IllegalArgumentException(""));
  }

  public Map tenantSlackInformation(String token, String host) {

    HttpClient.newClient("slack.com", 443)
      .enableWireLogging("hello-client", LogLevel.TRACE)
      .createGet("/api/team.info?token=" + token)
      .doOnNext(resp -> log.info(resp.toString()))
      .flatMap(resp -> resp.getContent()
        .map(bb -> bb.toString(Charset.defaultCharset())))
      .toBlocking()
      .forEach(log::info);

    //System.out.println(httpClient.retrieve(HttpRequest.GET("team.info?token="+token)).blockingFirst());
    //  retrieve = client.toBlocking().retrieve("/api/team.info?token=" + token);
    /*
    @Client("https://slack.com/api/")
  @Inject
  private RxHttpClient httpClient;
     */


    return null;
  }

  public void setTenantsFile(String tenantsFile) {
    this.tenantsFile = tenantsFile;
  }


  /*




  @Cacheable('slackPublicData')
  Map slack(String hostname) {
    findTenant(hostname)
      .map { publicData(it.token, it.teamDomain) }
      .orElse(emptyMap())
  }

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

  Map publicData(String token, String host) {
    log.info 'Searching public data in Slack for {}', host

    Map map = tenantSlackInformation(token, host)
    publicData(map)
  }

  private Predicate isSlackBot = { Map map ->
    'USLACKBOT' == map.id
  }

  private Predicate isActive = { Map map ->
    'active' == map.presence
  }

  Boolean isActiveUser(Map map) {
    isSlackBot.negate().and(isActive).test(map)
  }

  Map publicData(Map data) {
    int active = data.users.findAll {
      isActiveUser(it)
    }.size()

    int total = data.users.findAll {
      isSlackBot.negate().test(it)
    }.size()

    [
      name : data.team.name,
      logo : data.team.icon.image_132,
      users: [
        active: active,
        total : total
      ]
    ]
  }
   */
}
